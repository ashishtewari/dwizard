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
	 * @param key of type JsonString send via HeaderParams
	 * @return Object
	 */
	@GET
	@Path("/login")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getLoginDetails(@HeaderParam("loginDetails") String userDetails) {
		try{
			if(helper.isUserDetailsValidJson(userDetails) && helper.isUserDetailsContainsValidKeys(userDetails)){
				JSONObject jsonObj = helper.jsonParser(userDetails);
				String username = (String) jsonObj.get("userName");
				String password = MD5Encoding.encrypt((String) jsonObj.get("password"));
				long adminLevel = (long) jsonObj.get("adminLevel");
				List<Admin> admin_details = this.auth.login(username, password, adminLevel);
				if (admin_details.isEmpty()) {
					return new Reply(401, "Login Fail Invalid Credentials",
							new SubStatusReply(0, "Failure"));
				} else {
					return new Reply(200, "Test Success", new SubStatusReply(1, "Success"));
				}
			}
			else{
				return new Reply(400, "Bad Request, Give Valid Json/keys",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}		
	}

	/**
	 * This is the Post method accessed by path HOST/v1.0/admin/registerUser
	 * This method calls registerConsumer to create new consumers and later on success 
	 * calls assigningConsumerPermission to assign permissions 
	 * @param key of type String, Consists accessToken of Admin sent via HeaderParams
	 * @param request of type Json, sent via Body raw
	 * @return Object
	 */
	
	@SuppressWarnings({ "unchecked" })
	@POST
	@Path("/registerUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object registerUser(@HeaderParam("accessToken") String key,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(key);
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject jsonObj = helper.contextRequest(request);
				String generateduserAccessToken = helper.generateRandomAccessToken();
				String generatedpassword = helper.generateRandomPassword();
				jsonObj.put("accessToken",generateduserAccessToken);
				jsonObj.put("password", generatedpassword);
				if (((String) jsonObj.get("type")).equals("admin") && accessLevel == 1) {
					int id = registerAdmin(jsonObj);
					if (id != 0) {
						int status = assigningPermission(jsonObj, id);
						if(status == 1)
							return new Reply(206, "Successfully inserted and given permissions to admin",new PartialAdminDataReply(1,generateduserAccessToken,generatedpassword,(String)jsonObj.get("userName")));
						else
							return helper.checkStatus(status);
					} else {
						return new Reply(406, "user Already Exists",null);
					}
				} else if (((String) jsonObj.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
					int id = registerConsumer(jsonObj);
					if (id != 0) {
						int status = assigningPermission(jsonObj, id);
						if(status == 1)
							return new Reply(206, "Successfully inserted and given permissions to Consumer",new PartialConsumerDataReply(1,generateduserAccessToken,(String)jsonObj.get("userName")));
						else
							return helper.checkStatus(status);
					} else {
						return new Reply(406, "user Already Exists",null);
					}
				} else {
					return new Reply(400, "Bad Request, Give Valid key of \"type\" or check your adminLevel",null);
				}
			} else {
				return new Reply(401, "You are not autherized",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}
				
	}

	/**
	 * This is the Post method accessed by path HOST/v1.0/admin/assignPermissions
	 * This method calls assigningConsumerPermission to assign permissions if user already exists
	 * @param key of type String, Consists accessToken of Admin sent via HeaderParams
	 * @param request of type Json, sent via Body raw
	 * @return Object
	 */
	@PUT
	@Path("/updatePermissions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object assignPermissions(@HeaderParam("accessToken") String key,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(key);
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject jsonObj = helper.contextRequest(request);
				if (((String) jsonObj.get("type")).equals("admin") && accessLevel == 1) {
					if(isAdminUserNameAlreadyExists((String) jsonObj.get("userName"))){
						int id = this.auth.getAdminId((String) jsonObj.get("userName"));
						int status = assigningPermission(jsonObj, id);
						return helper.checkStatus(status);
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else if (((String) jsonObj.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
					if(isConsumerUserNameAlreadyExists((String) jsonObj.get("userName"))){
						int id = this.auth.getConsumerId((String) jsonObj.get("userName"));
						int status = assigningPermission(jsonObj, id);
						return helper.checkStatus(status);
					}
					else{
						return new Reply(404, "UserName not found", null);
					}
				} else {
					return new Reply(400, "Bad Request, Give Valid key of type or check your adminLevel",null);
				}
			}else {
				return new Reply(401, "You are not autherized",null);
			}
		}catch(NullPointerException e){
			return new Reply(401, "You are not autherized",null);
		}
			
	}

	/**
	 * Checks if consumer userName is already present in table. 
	 * We are using this to avoid duplicates
	 * @param userName
	 * @return boolean
	 */
	private boolean isConsumerUserNameAlreadyExists(String userName) {
		if (userName.equals(this.auth.isConsumerUserNameAlreadyExists(userName))) {
			return true;
		} else
			return false;
	}
	
	private boolean isAdminUserNameAlreadyExists(String userName){
		if (userName.equals(this.auth.isAdminUserNameAlreadyExists(userName))) {
			return true;
		} else
			return false;
	}

	/**
	 * This method registers new consumers
	 * @param obj
	 * @return int
	 */
	private int registerConsumer(JSONObject jsonObj) {
		System.out.println("In register Consumer");
		if (jsonObj.containsKey("userName")	&& jsonObj.containsKey("accessToken") && jsonObj.containsKey("countAssigned") && helper.emailIsValid((String) jsonObj.get("userName")) && !isConsumerUserNameAlreadyExists((String) jsonObj.get("userName"))) {
			System.out.println("In IF");
			int id = this.auth.addConsumer((String) jsonObj.get("userName"),(String) jsonObj.get("accessToken"),(long) jsonObj.get("countAssigned"));
			System.out.println("Exeuted Query");
			return id;
		} else
			return 0;
	}
	
	/**
	 * This method registers new Admins
	 * @param obj
	 * @return
	 */
	public int registerAdmin(JSONObject jsonObj){
		if (jsonObj.containsKey("userName")	&& jsonObj.containsKey("accessToken") && jsonObj.containsKey("password") && helper.emailIsValid((String) jsonObj.get("userName")) && !isAdminUserNameAlreadyExists((String) jsonObj.get("userName"))) {
			int id = this.auth.addAdmin((String) jsonObj.get("userName"),MD5Encoding.encrypt((String) jsonObj.get("password")),(String) jsonObj.get("accessToken"));
			return id;
		} else
			return 0;
	}
	
	/**
	 * This method checks whether there is any resource permission is assigned to consumer or not
	 * @param resourceId
	 * @param consumerId
	 * @return
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
	 * @param to
	 * @param type
	 * @param resourceId
	 * @param consumerId
	 * @param permissions
	 * @return
	 */
	private int givePermission(String to,String type,long resourceId,long userId,JSONObject permissions){
		long get = 0,post = 0,put = 0,delete = 0;
		JSONArray requestArray = null;
		try{
			requestArray = (JSONArray) permissions.get("permission");
		}catch(ClassCastException e){
			return 4;
		}		
		String[] permission = new String[requestArray.size()];
		for (int k = 0; k < permission.length; k++)
			permission[k] = (String) requestArray.get(k);
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
				return 5;
			}
		}
		int typeOfQuery = 0;
		if(type.equals("insert")){
			if(to.equals("consumer"))
				this.auth.insertConsumerPermission(resourceId,userId, get, post, put, delete);
			else
				this.auth.insertAdminPermission(resourceId,userId, get, post, put, delete);
			typeOfQuery = 1;
		}else if(type.equals("update")){					
			if(to.equals("consumer"))
				this.auth.updateConsumerPermission(resourceId,userId, get, post, put, delete);
			else
				this.auth.updateAdminPermission(resourceId,userId, get, post, put, delete);
			typeOfQuery = 6;
		}
		return typeOfQuery;		
	}

	/**
	 * This method assigns resource permissions to the consumers/admin
	 * @param obj
	 * @param consumerId
	 * @return int
	 */
	private int assigningPermission(JSONObject jsonObj, int userId) {
		int result_status = 0;
		if (jsonObj.containsKey("permissions")) {
			JSONArray resources = (JSONArray) jsonObj.get("permissions");
			if (resources.size() == 0) {
				return 2;
			} else {
				for (int i = 0; i < resources.size(); i++) {
					JSONObject permissions = (JSONObject) resources.get(i);
					if (permissions.containsKey("resourceId") && permissions.containsKey("permission") && isValidResourceId((long)permissions.get("resourceId"))) {
						long resourceId = (long) permissions.get("resourceId");
						if(isPermissionExists((String)jsonObj.get("type"),resourceId,(long)userId)){
							result_status = givePermission((String)jsonObj.get("type"),"update",resourceId,(long)userId,permissions);
							if(result_status == 4 || result_status == 5)
								return result_status;
						}
						else{
							result_status = givePermission((String)jsonObj.get("type"),"insert",resourceId,(long)userId,permissions);
							if(result_status == 4 || result_status == 5)
								return result_status;
						}						
					} else {
						return 3;
					}
				}
				return result_status;
			}
		} else
			return 0;
	}
	
	public boolean isValidResourceId(long resourceId){
		if(this.auth.isValidResource(resourceId) != 0)
			return true;
		else
			return false;
	}

}
