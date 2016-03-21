package com.mebelkart.api.admin.v1.resources;

import java.net.ConnectException;
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
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.helper.HelperMethods;
import com.mebelkart.api.admin.v1.api.AdminPrivilagesResponse;
import com.mebelkart.api.admin.v1.api.AdminResponse;
import com.mebelkart.api.admin.v1.api.ConsumerResponse;
import com.mebelkart.api.admin.v1.api.UserPrivilagesResponse;
import com.mebelkart.api.util.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.Privilages;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.util.HandleException;

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
	 * exception
	 */
	HandleException exception = new HandleException();
	
	/**
	 * helper
	 */
	HelperMethods helper = new HelperMethods();	
	
	/**
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(mkApiApplication.class);
	
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
				List<Admin> admin_details = this.auth.login(username, password);
				if (!admin_details.get(0).getA_user_name().equals(username)) {
					log.warn("unauthorized request in login function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details", null);
				} else {
					AdminPrivilagesResponse privilages = new AdminPrivilagesResponse();
					if(admin_details.get(0).getA_admin_level() == 1){						
						privilages.setSuperAdminPrivilages();
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
					}
					else if(admin_details.get(0).getA_admin_level() == 2){
						privilages.setAdminPrivilages();
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
					}
					else{
						log.warn("Unauthorized data in login function");
						exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
						return exception.getException("your admin credentials are not acceptable", null);
					}
				}
			}
			else{
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("give valid JsonData/keys", null);
			}
		}catch(NullPointerException e){
		    exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(IndexOutOfBoundsException e){
			log.warn("unauthorized request in login function");
			exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
			return exception.getException("please provide valid details", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in login function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
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
					int rowId = registerAdmin(rawData);
					if (rowId != 0) {
						int status = assigningPermission(rawData, rowId);
						if(status == 1)
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						else
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
					} else {
						log.warn("Not acceptable data in registerUser function for admin");
						exception = new HandleException(Response.Status.NOT_ACCEPTABLE.getStatusCode(),Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
						return exception.getException("check data once again", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					int rowId = registerConsumer(rawData);
					if (rowId != 0) {
						int status = assigningPermission(rawData, rowId);
						if(status == 1)
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
						else
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
					} else {
						log.warn("Not acceptable data in registerUser function for consumer");
						exception = new HandleException(Response.Status.NOT_ACCEPTABLE.getStatusCode(),Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
						return exception.getException("check data once again", null);
					}
				} else {
					log.warn("Unauthorized data in registerUser function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details also check your adminLevel/activeState", null);
				}
			} else {
				log.warn("Unauthorized data in registerUser function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your admin credentials are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in registerUser function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
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
					if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
						int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_user_admin");
						int status = assigningPermission(rawData, rowId);
						return helper.checkStatus(status);
					}
					else{
						log.warn("Not found data in updatePermissions function for admin");
						exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
						return exception.getException("give valid user name", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
						int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_consumer");
						int status = assigningPermission(rawData, rowId);
						return helper.checkStatus(status);
					}
					else{
						log.warn("Not found data in updatePermissions function for consumer");
						exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
						return exception.getException("give valid user name", null);
					}
				} else {
					log.warn("Unauthorized data in updatePermissions function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details also check your adminLevel/activeState", null);
				}
			}else {
				log.warn("Unauthorized data in updatePermissions function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your admin credentials are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in updatePermissions function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
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
					if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
						this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_user_admin");
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
					}
					else{
						log.warn("Not found data in changeUserActiveStatus function for admin");
						exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
						return exception.getException("give valid user name", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
						this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_consumer");
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
					}
					else{
						log.warn("Not found data in changeUserActiveStatus function for consumer");
						exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
						return exception.getException("give valid user name", null);
					}
				} else {
					log.warn("Unauthorized data in changeUserActiveStatus function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details also check your adminLevel/activeState", null);
				}
			}else {
				log.warn("Unauthorized data in changeUserActiveStatus function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your admin credentials are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in changeUserActiveStatus function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
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
					if(rawData.containsKey("status")){
						List<UserStatus> userStatusDetails = this.auth.getUsersDetailsWithStatus((long) rawData.get("status"),"mk_api_user_admin");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),userStatusDetails);
					}
					else{
						List<UserStatus> allUsersStatusDetails = this.auth.getUsersDetailsWithoutStatus("mk_api_user_admin");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase()+". Retrieved all admin status details", allUsersStatusDetails);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(rawData.containsKey("status")){
						List<UserStatus> userStatusDetails = this.auth.getUsersDetailsWithStatus((long) rawData.get("status"),"mk_api_consumer");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),userStatusDetails);
					}
					else{
						List<UserStatus> allUsersStatusDetails = this.auth.getUsersDetailsWithoutStatus("mk_api_consumer");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase()+". Retrieved all consumer status details", allUsersStatusDetails);
					}
				} else {
					log.warn("Unauthorized data in getUsersStatus function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details also check your adminLevel/activeState", null);
				}
			}else {
				log.warn("Unauthorized data in getUsersStatus function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your admin_credentials/user_details are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in getUsersStatus function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}	
	}
	
	@GET
	@Path("/getUserPrivileges")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getUserPrivileges(@HeaderParam("accessToken") String apikey,@HeaderParam("accessParam") String userDetails){
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if((accessLevel == 1 || accessLevel == 2) && helper.isUserDetailsValidJson(userDetails)){
				JSONObject rawData = helper.jsonParser(userDetails);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isAdminActive(apikey)) {
					if(rawData.containsKey("userName") && isUserNameAlreadyExists("admin",(String)rawData.get("userName"))){
						int adminId = this.auth.getUserId((String)rawData.get("userName"), "mk_api_user_admin");
						List<Privilages> userDetail = this.auth.getUserPrivileges((long) adminId,"mk_api_resources_admin_permission","a_admin_id");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),new UserPrivilagesResponse((String)rawData.get("userName"),userDetail));
					}
					else{
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("give valid keys", null);
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isAdminActive(apikey)) {
					if(rawData.containsKey("userName") && isUserNameAlreadyExists("consumer",(String)rawData.get("userName"))){
						int consumerId = this.auth.getUserId((String)rawData.get("userName"), "mk_api_consumer");
						List<Privilages> userDetail = this.auth.getUserPrivileges((long) consumerId,"mk_api_resources_consumer_permission","a_consumer_id");
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),new UserPrivilagesResponse((String)rawData.get("userName"),userDetail));
					}
					else{
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("give valid keys", null);
					}
				} else {
					log.warn("Unauthorized data in getUserPrivileges function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("please provide valid details also check your adminLevel/activeState", null);
				}
			}else {
				log.warn("Unauthorized data in getUserPrivileges function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your admin_credentials/user_details are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused exception in getUserPrivileges function");
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("Connection refused exception caused", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}
	}

	/**
	 * Checks if Users userName is already present in table. 
	 * We are using this to avoid duplicates
	 * @param userName the users name
	 * @param typeOfUser admin/consumer
	 * @return boolean
	 */
	private boolean isUserNameAlreadyExists(String typeOfUser,String userName) {
		if(typeOfUser.equals("consumer")){
			if (userName.equals(this.auth.isUserNameAlreadyExists(userName,"mk_api_consumer"))) {
				return true;
			} else
				return false;
		}
		else if(typeOfUser.equals("admin")){
			if (userName.equals(this.auth.isUserNameAlreadyExists(userName,"mk_api_user_admin"))) {
				return true;
			} else
				return false;
		}
		else return false;
	}
	
	/**
	 * Checks whether the admin is in active state or not
	 * @param apikey consists of his accessToken
	 * @return true/false
	 */
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
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("countAssigned") && helper.emailIsValid((String) jsonData.get("userName")) && !isUserNameAlreadyExists("consumer",(String) jsonData.get("userName"))) {
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
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("password") && helper.emailIsValid((String) jsonData.get("userName")) && !isUserNameAlreadyExists("admin",(String) jsonData.get("userName"))) {
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
			if(this.auth.isUserPermissionExists(resourceId, userId,"mk_api_resources_consumer_permission","a_consumer_id") != 0)
				return true;
			else
				return false;
		}
		else if(userType.equals("admin")){
			if(this.auth.isUserPermissionExists(resourceId, userId,"mk_api_resources_admin_permission","a_admin_id") != 0)
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
				this.auth.insertUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_consumer_permission","a_consumer_id");
			else
				this.auth.insertUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_admin_permission","a_admin_id");
			//Refer to checkstatus method in HelperMethods class
			typeOfQuery = 1;
		}else if(type.equals("update")){					
			if(to.equals("consumer"))
				this.auth.updateUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_consumer_permission","a_consumer_id");
			else
				this.auth.updateUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_admin_permission","a_admin_id");
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
