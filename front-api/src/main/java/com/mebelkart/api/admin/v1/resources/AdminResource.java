package com.mebelkart.api.admin.v1.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.util.HelperMethods;
import com.mebelkart.api.admin.v1.api.PartialAdminDataReply;
import com.mebelkart.api.admin.v1.api.PartialConsumerDataReply;
import com.mebelkart.api.admin.v1.api.SubStatusReply;
import com.mebelkart.api.admin.v1.api.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;

/**
 * @author Tinku
 *
 */
@Path("v1.0/admin")
public class AdminResource {

	/**
	 * auth
	 */
	AdminDAO auth;
	/**
	 * helper
	 */
	HelperMethods helper = new HelperMethods();

	/**
	 * @param auth
	 */
	public AdminResource(AdminDAO auth) {
		this.auth = auth;
	}

	/**
	 * This is the Get method accessed by admin to see their privilages
	 * @param userDetails of type JsonString send via HeaderParams
	 * @return Object of type JSON
	 */
	@GET
	@Path("/login")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getLoginDetails(@HeaderParam("loginDetails") String userDetails) {
		try{
			if(helper.isUserDetailsValidJson(userDetails) && helper.isUserDetailsContainsValidKeys(userDetails)){
				JSONObject userDetailsObj = helper.jsonParser(userDetails);
				String username = (String) userDetailsObj.get("userName");
				String password = MD5Encoding.encrypt((String) userDetailsObj.get("password"));
				long adminLevel = (long) userDetailsObj.get("adminLevel");
				List<Admin> admin_details = this.auth.login(username, password, adminLevel);
				if (!admin_details.get(0).getA_user_name().equals(username)) {
					return new Reply(401, "Login Fail Invalid Credentials",
							new SubStatusReply(0, "Failure"));
				} else {
					return new Reply(200, "Test Success", new SubStatusReply(1, "Success"));
				}
			}
			else{
				return new Reply(400, "Bad Request, give Valid Json/keys",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}catch(ClassCastException e){
			return new Reply(400, "Bad Request, give Valid Json/values",null);
		}catch(IndexOutOfBoundsException e){
			return new Reply(400, "Bad Request, There are no users matching your requirments",null);
		}
	}

	/**
	 * This is the Post method accessed by path HOST/v1.0/admin/registerUser
	 * This method calls registerConsumer to create new consumers and later on success 
	 * calls assigningConsumerPermission to assign permissions 
	 * @param apikey of type String, Consists accessToken of Admin sent via HeaderParams
	 * @param request of type Json, sent via Body raw
	 * @return Object of type of JSON
	 */	
	@SuppressWarnings({ "unchecked" })
	@POST
	@Path("/registerUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object registerUser(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = helper.contextRequestParser(request);
				String generateduserAccessToken = helper.generateUniqueAccessToken();
				String generatedpassword = helper.generateRandomPassword();
				rawData.put("accessToken",generateduserAccessToken);
				rawData.put("password", generatedpassword);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isAdminActive(apikey)) {
					int id = registerAdmin(rawData);
					if (id != 0) {
						int status = assigningPermission(rawData, id);
						if(status == 1)
							return new Reply(206, "Successfully inserted and given permissions to admin",new PartialAdminDataReply(1,generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						else
							return helper.checkStatus(status,"Successfully inserted but ");
					} else {
						return new Reply(406, "user Already Exists",null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					int rowId = registerConsumer(rawData);
					if (rowId != 0) {
						int status = assigningPermission(rawData, rowId);
						if(status == 1)
							return new Reply(206, "Successfully inserted and given permissions to Consumer",new PartialConsumerDataReply(1,generateduserAccessToken,(String)rawData.get("userName")));
						else
							return helper.checkStatus(status,"Successfully inserted but ");
					} else {
						return new Reply(406, "user Already Exists",null);
					}
				} else {
					return new Reply(400, "Bad Request, give Valid key of type or check your adminLevel/activeState ",null);
				}
			} else {
				return new Reply(401, "You are not autherized",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}catch(ClassCastException e){
			return new Reply(400, "Bad Request, give Valid Json/values",null);
		}				
	}

	/**
	 * This is the Post method accessed by path HOST/v1.0/admin/assignPermissions
	 * This method calls assigningConsumerPermission to assign permissions if user already exists
	 * @param apikey of type String, Consists accessToken of Admin sent via HeaderParams
	 * @param request of type Json, sent via Body raw
	 * @return Object of type JSON
	 */
	@PUT
	@Path("/updatePermissions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object assignPermissions(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = helper.contextRequestParser(request);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isAdminActive(apikey)) {
					if(isAdminUserNameAlreadyExists((String) rawData.get("userName"))){
						int rowId = this.auth.getAdminId((String) rawData.get("userName"));
						int status = assigningPermission(rawData, rowId);
						return helper.checkStatus(status,"");
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(isConsumerUserNameAlreadyExists((String) rawData.get("userName"))){
						int rowId = this.auth.getConsumerId((String) rawData.get("userName"));
						int status = assigningPermission(rawData, rowId);
						return helper.checkStatus(status,"");
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else {
					return new Reply(400, "Bad Request, give Valid key of type or check your adminLevel/activeState",null);
				}
			}else {
				return new Reply(401, "You are not autherized",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}catch(ClassCastException e){
			return new Reply(400, "Bad Request, give Valid Json/values",null);
		}			
	}
	
	/**
	 * This is the Put method accessed by path HOST/v1.0/admin/changeUserActiveStatus
	 * This method used to change user active status
	 * @param apikey
	 * @param request
	 * @return
	 */
	@PUT
	@Path("/changeUserActiveStatus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object changeUserStatus(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request){
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = helper.contextRequestParser(request);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isAdminActive(apikey)) {
					if(isAdminUserNameAlreadyExists((String) rawData.get("userName"))){
						this.auth.changeAdminActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"));
						return new Reply(206, "Successfully updated active status of admin",null);
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(isConsumerUserNameAlreadyExists((String) rawData.get("userName"))){
						this.auth.changeConsumerActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"));
						return new Reply(206, "Successfully updated active status of consumer",null);
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else {
					return new Reply(400, "Bad Request, give Valid key of type or check your adminLevel/activeState",null);
				}
			}else {
				return new Reply(401, "You are not autherized",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized,give valid details",null);
		}catch(ClassCastException e){
			return new Reply(400, "Bad Request,give Valid Json/values",null);
		}	
	}
	
	/**
	 * This is the Get method accessed by path HOST/v1.0/admin/getUsersStatus
	 * It gives userName and isActive status of every user
	 * @param apikey is passed from header
	 * @param userDetails is passed from header, contains jsonString
	 * @return
	 */
	@GET
	@Path("/getUsersStatus")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getUsersStatus(@HeaderParam("accessToken") String apikey,@HeaderParam("accessParam") String userDetails) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if((accessLevel == 1 || accessLevel == 2) && helper.isUserDetailsValidJson(userDetails)){
				JSONObject rawData = helper.jsonParser(userDetails);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isAdminActive(apikey)) {
					System.out.println("In admin");
					if(rawData.containsKey("status")){
						System.out.println("in if");
						List<UserStatus> userStatusDetails = this.auth.getAdminsStatus((long) rawData.get("status"));
						return new Reply(200, "The request is OK.",userStatusDetails);
					}
					else{
						System.out.println("in else");
						List<UserStatus> allUsersStatusDetails = getAllAdminUsersStatus();
						return new Reply(200, "The request is OK. Retrieved all admin status details", allUsersStatusDetails);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(rawData.containsKey("status")){
						List<UserStatus> userStatusDetails = this.auth.getConsumersStatus((long) rawData.get("status"));
						return new Reply(200, "The request is OK.",userStatusDetails);
					}
					else{
						List<UserStatus> allUsersStatusDetails = getAllConsumerUsersStatus();
						return new Reply(200, "The request is OK. Retrieved all consumer status details", allUsersStatusDetails);
					}
				} else {
					return new Reply(400, "Bad Request, give Valid key of type or check your adminLevel/activeState",null);
				}
			}else {
				return new Reply(401, "It is not valid Json format",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized,give valid details",null);
		}catch(ClassCastException e){
			return new Reply(400, "Bad Request,give Valid Json/values",null);
		}	
	}
	
	/**
	 * This method returns active status of every admin user
	 * @return
	 */
	private List<UserStatus> getAllAdminUsersStatus(){
		List<UserStatus> nonActiveUserStatusDetails = this.auth.getAdminsStatus(0);
		List<UserStatus> activeUserStatusDetails = this.auth.getAdminsStatus(1);
		activeUserStatusDetails.addAll(nonActiveUserStatusDetails);
		return activeUserStatusDetails;
	}
	
	/**
	 * This method returns active status of every consumer user
	 * @return
	 */
	private List<UserStatus> getAllConsumerUsersStatus(){
		List<UserStatus> nonActiveUserStatusDetails = this.auth.getConsumersStatus(0);
		List<UserStatus> activeUserStatusDetails = this.auth.getConsumersStatus(1);
		activeUserStatusDetails.addAll(nonActiveUserStatusDetails);
		return activeUserStatusDetails;
	}
	
	/**
	 * Checks if Consumer userName is already present in table. 
	 * We are using this to avoid duplicates
	 * @param userNameConsumer of Consumer
	 * @return boolean
	 */
	private boolean isConsumerUserNameAlreadyExists(String userNameConsumer) {
		if (userNameConsumer.equals(this.auth.isConsumerUserNameAlreadyExists(userNameConsumer))) {
			return true;
		} else
			return false;
	}
	
	/**
	 * Checks if Admin userName is already present in table.
	 * We are using this to avoid duplicates 
	 * @param userNameAdmin
	 * @return true/false
	 */
	private boolean isAdminUserNameAlreadyExists(String userNameAdmin){
		if (userNameAdmin.equals(this.auth.isAdminUserNameAlreadyExists(userNameAdmin))) {
			return true;
		} else
			return false;
	}
	
	private boolean isAdminActive(String apikey){
		if(this.auth.isAdminInActiveState(apikey) == 1)
			return true;
		else
			return false;
	}

	/**
	 * This method registers new consumers
	 * @param jsonData of consumer details
	 * @return int newly inserted row ID
	 */
	private int registerConsumer(JSONObject jsonData) {
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("countAssigned") && helper.emailIsValid((String) jsonData.get("userName")) && !isConsumerUserNameAlreadyExists((String) jsonData.get("userName"))) {
			int id = this.auth.addConsumer((String) jsonData.get("userName"),(String) jsonData.get("accessToken"),(long) jsonData.get("countAssigned"));
			return id;
		} else
			return 0;
	}
	
	/**
	 * This method registers new Admins
	 * @param jsonData of admin details
	 * @return int newly inserted row ID
	 */
	public int registerAdmin(JSONObject jsonData){
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("password") && helper.emailIsValid((String) jsonData.get("userName")) && !isAdminUserNameAlreadyExists((String) jsonData.get("userName"))) {
			int id = this.auth.addAdmin((String) jsonData.get("userName"),MD5Encoding.encrypt((String) jsonData.get("password")),(String) jsonData.get("accessToken"));
			return id;
		} else
			return 0;
	}
	
	/**
	 * This method checks whether there is any resource permission is assigned to user or not
	 * @param userType consumer/admin
	 * @param resourceId contains package ID
	 * @param userId contains row ID's of consumer/admin
	 * @return boolean
	 */
	private boolean isPermissionExists(String userType,long resourceId,long userId){
		if(userType.equals("consumer")){
			if(this.auth.isConsumerPermissionExists(resourceId, userId) != 0)
				return true;
			else
				return false;
		}
		else if(userType.equals("admin")){
			if(this.auth.isAdminPermissionExists(resourceId, userId) != 0)
				return true;
			else
				return false;
		}
		else
			return false;		
	}
	
	/**
	 * This method will give permission to Admin and Consumer in both Update and Insert operations
	 * @param to consists of consumer/admin
	 * @param type consists of insert/update
	 * @param resourceId contains package ID
	 * @param userId contains row ID's of consumer/admin
	 * @param permissions has an array of permissions set in it (GET/POST/PUT/DELETE)
	 * @return int 1 for insert/6 for Update
	 */
	private int givePermission(String to,String type,long resourceId,long userId,JSONObject permissions){
		long get = 0,post = 0,put = 0,delete = 0;
		JSONArray permissionJsonArray = null;
		try{
			permissionJsonArray = (JSONArray) permissions.get("permission");
		}catch(ClassCastException e){
			//Refer to checkstatus method in HelperMethods class
			return 4;
		}		
		String[] permission = new String[permissionJsonArray.size()];
		for (int k = 0; k < permission.length; k++)
			permission[k] = (String) permissionJsonArray.get(k);
		for (int j = 0; j < permission.length; j++) {
			if (permission[j].toUpperCase().equals("GET")) {
				get = 1;
			} else if (permission[j].toUpperCase().equals(
					"POST")) {
				post = 1;
			} else if (permission[j].toUpperCase().equals(
					"PUT")) {
				put = 1;
			} else if (permission[j].toUpperCase().equals(
					"DELETE")) {
				delete = 1;
			} else {
				//Refer to checkstatus method in HelperMethods class
				return 5;
			}
		}
		int typeOfQuery = 0;
		if(type.equals("insert")){
			if(to.equals("consumer"))
				this.auth.insertConsumerPermission(resourceId,userId, get, post, put, delete);
			else
				this.auth.insertAdminPermission(resourceId,userId, get, post, put, delete);
			//Refer to checkstatus method in HelperMethods class
			typeOfQuery = 1;
		}else if(type.equals("update")){					
			if(to.equals("consumer"))
				this.auth.updateConsumerPermission(resourceId,userId, get, post, put, delete);
			else
				this.auth.updateAdminPermission(resourceId,userId, get, post, put, delete);
			//Refer to checkstatus method in HelperMethods class
			typeOfQuery = 6;
		}
		return typeOfQuery;		
	}

	/**
	 * This method assigns resource permissions to the consumers/admin
	 * @param jsonData of permissions details 
	 * @param userId contains row ID's of consumer/admin
	 * @return int
	 */
	private int assigningPermission(JSONObject jsonData, int userId) {
		int result_status = 0;
		if (jsonData.containsKey("permissions")) {
			JSONArray resources = (JSONArray) jsonData.get("permissions");
			if (resources.size() == 0) {
				//Refer to checkstatus method in HelperMethods class
				return 2;
			} else {
				for (int i = 0; i < resources.size(); i++) {
					JSONObject permissions = (JSONObject) resources.get(i);
					if (permissions.containsKey("resourceId") && permissions.containsKey("permission") && isValidResourceId((long)permissions.get("resourceId"))) {
						long resourceId = (long) permissions.get("resourceId");
						if(isPermissionExists((String)jsonData.get("type"),resourceId,(long)userId)){
							result_status = givePermission((String)jsonData.get("type"),"update",resourceId,(long)userId,permissions);
							//Refer to checkstatus method in HelperMethods class
							if(result_status == 4 || result_status == 5)
								return result_status;
						}
						else{
							result_status = givePermission((String)jsonData.get("type"),"insert",resourceId,(long)userId,permissions);
							//Refer to checkstatus method in HelperMethods class
							if(result_status == 4 || result_status == 5)
								return result_status;
						}						
					} else {
						//Refer to checkstatus method in HelperMethods class
						return 3;
					}
				}
				return result_status;
			}
		} else
			return 0;
	}
	
	/**
	 * Checks whether the resource id is valid api package or not
	 * @param resourceId contains resourceId of api
	 * @return true/false
	 */
	public boolean isValidResourceId(long resourceId){
		if(this.auth.isValidResource(resourceId) != 0)
			return true;
		else
			return false;
	}
}
