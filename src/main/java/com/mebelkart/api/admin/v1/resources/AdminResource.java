package com.mebelkart.api.admin.v1.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
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
	public Object getLoginDetails(@HeaderParam("parameters") String userDetails) {
		JSONObject obj = helper.jsonParser(userDetails);
		String username = (String) obj.get("a_user_name");
		String password = MD5Encoding.encrypt((String) obj.get("a_password"));
		long level = (long) obj.get("a_admin_level");
		List<Admin> admin_details = this.auth.login(username, password, level);
		if (admin_details.isEmpty()) {
			return new Reply(401, "Login Fail Invalid Credentials",
					new SubStatusReply(0, "Failure"));
		} else {
			return new Reply(200, "Test Success", new SubStatusReply(1, "Success"));
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
		int accessLevel = this.auth.validate(key);
		JSONObject obj = helper.contextRequest(request);
		String generateduserAccessToken = helper.generateRandomAccessToken();
		String generatedpassword = helper.generateRandomPassword();
		obj.put("accessToken",generateduserAccessToken);
		obj.put("password", generatedpassword);
		if (((String) obj.get("type")).equals("admin") && accessLevel == 1) {
			int id = registerAdmin(obj);
			if (id != 0) {
				int status = assigningPermission(obj, id);
				if(status == 1)
					return new Reply(206, "Successfully inserted and given permissions to admin",new PartialAdminDataReply(1,generateduserAccessToken,generatedpassword,(String)obj.get("userName")));
				else
					return helper.checkStatus(status);
			} else {
				return new Reply(406, "user Already Exists",new SubStatusReply(0,"Failure"));
			}
		} else if (((String) obj.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
			int id = registerConsumer(obj);
			if (id != 0) {
				int status = assigningPermission(obj, id);
				if(status == 1)
					return new Reply(206, "Successfully inserted and given permissions to Consumer",new PartialConsumerDataReply(1,generateduserAccessToken,(String)obj.get("userName")));
				else
					return helper.checkStatus(status);
			} else {
				return new Reply(406, "user Already Exists",new SubStatusReply(0,"Failure"));
			}
		} else {
			return new Reply(401, "You are not autherized", new SubStatusReply(0,"Failure"));
		}
	}

	/**
	 * This is the Post method accessed by path HOST/v1.0/admin/assignPermissions
	 * This method calls assigningConsumerPermission to assign permissions if user already exists
	 * @param key of type String, Consists accessToken of Admin sent via HeaderParams
	 * @param request of type Json, sent via Body raw
	 * @return Object
	 */
	@POST
	@Path("/assignPermissions")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object assignPermissions(@HeaderParam("accessToken") String key,@Context HttpServletRequest request) {
		int accessLevel = this.auth.validate(key);
		JSONObject obj = helper.contextRequest(request);
		if (((String) obj.get("type")).equals("admin") && accessLevel == 1) {
			if(isAdminUserNameAlreadyExists((String) obj.get("adminEmail"))){
				int id = this.auth.getAdminId((String) obj.get("adminEmail"));
				int status = assigningPermission(obj, id);
				return helper.checkStatus(status);
			}
			else{
				return new Reply(404, "UserName not found", new SubStatusReply(0,"Failure"));
			}
		} else if (((String) obj.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
			if(isConsumerUserNameAlreadyExists((String) obj.get("userName"))){
				int id = this.auth.getConsumerId((String) obj.get("userName"));
				int status = assigningPermission(obj, id);
				return helper.checkStatus(status);
			}
			else{
				return new Reply(404, "UserName not found", new SubStatusReply(0,"Failure"));
			}
		} else {
			return new Reply(400, "Specify Correct Type of User", new SubStatusReply(0,"Failure"));
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
	private int registerConsumer(JSONObject obj) {
		System.out.println("In register Consumer");
		if (obj.containsKey("userName")	&& obj.containsKey("accessToken") && obj.containsKey("countAssigned") && helper.emailIsValid((String) obj.get("userName")) && !isConsumerUserNameAlreadyExists((String) obj.get("userName"))) {
			System.out.println("In IF");
			int id = this.auth.addConsumer((String) obj.get("userName"),(String) obj.get("accessToken"),(long) obj.get("countAssigned"));
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
	public int registerAdmin(JSONObject obj){
		System.out.println("In register Admin");
		if (obj.containsKey("userName")	&& obj.containsKey("accessToken") && obj.containsKey("password") && helper.emailIsValid((String) obj.get("userName")) && !isAdminUserNameAlreadyExists((String) obj.get("userName"))) {
			System.out.println("In IF");
			int id = this.auth.addAdmin((String) obj.get("userName"),MD5Encoding.encrypt((String) obj.get("password")),(String) obj.get("accessToken"));
			System.out.println("Exeuted Query");
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
	private boolean isPermissionExists(long resourceId,long consumerId){
		if(this.auth.isPermissionExists(resourceId, consumerId) != 0){
			return true;
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
	private int givePermission(String to,String type,long resourceId,long consumerId,JSONObject permissions){
		long get = 0,post = 0,put = 0,delete = 0;
		JSONArray requestArray = (JSONArray) permissions.get("permission");
		String[] permission = new String[requestArray.size()];
		for (int k = 0; k < permission.length; k++)
			permission[k] = (String) requestArray.get(k);
		if (permission.length == 0) {
			return 4;
		} else {
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
			if(to.equals("consumer")){
				if(type.equals("insert"))
					this.auth.insertConsumerPermission(resourceId,consumerId, get, post, put, delete);
				else if(type.equals("update"))
					this.auth.updateConsumerPermission(resourceId,consumerId, get, post, put, delete);
			}else if(to.equals("admin")){
				if(type.equals("insert"))
					this.auth.insertAdminPermission(resourceId,consumerId, get, post, put, delete);
				else if(type.equals("update"))
					this.auth.updateAdminPermission(resourceId,consumerId, get, post, put, delete);
			}
			return 1;
		}
	}

	/**
	 * This method assigns resource permissions to the consumers/admin
	 * @param obj
	 * @param consumerId
	 * @return int
	 */
	private int assigningPermission(JSONObject obj, int consumerId) {
		if (obj.containsKey("permissions")) {
			JSONArray resources = (JSONArray) obj.get("permissions");
			if (resources.size() == 0) {
				return 2;
			} else {
				for (int i = 0; i < resources.size(); i++) {
					JSONObject permissions = (JSONObject) resources.get(i);
					if (permissions.containsKey("resourceId") && permissions.containsKey("permission") && isValidResourceId((long)permissions.get("resourceId"))) {
						long resourceId = (long) permissions.get("resourceId");
						int result_status = 0;
						if(isPermissionExists(resourceId,(long)consumerId)){
							result_status = givePermission((String)obj.get("type"),"update",resourceId,(long)consumerId,permissions);
							if(result_status == 4 || result_status == 5)
								return result_status;
						}
						else{
							result_status = givePermission((String)obj.get("type"),"insert",resourceId,(long)consumerId,permissions);
							if(result_status == 4 || result_status == 5)
								return result_status;
						}						
					} else {
						return 3;
					}
				}
				return 1;
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
