package com.mebelkart.api.admin.v1.resources;

import java.net.ConnectException;
import java.util.ArrayList;
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

import com.mebelkart.api.admin.v1.dao.AdminDAO;
import com.mebelkart.api.admin.v1.helper.HelperMethods;
import com.mebelkart.api.admin.v1.api.AdminPrivilagesResponse;
import com.mebelkart.api.admin.v1.api.AdminResponse;
import com.mebelkart.api.admin.v1.api.ConsumerResponse;
import com.mebelkart.api.admin.v1.api.UserPrivilagesResponse;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.Privilages;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.util.exceptions.HandleException;

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
	static Logger log = LoggerFactory.getLogger(AdminResource.class);
	
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
	public Object getLoginDetails(@HeaderParam("loginDetails") String userDetails,@HeaderParam("accessToken") String apikey) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","get","login");
				if(accessControl == 1){
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
								List<String> resourcePrivilages = this.auth.getResourceNames("null");
								privilages.setSuperAdminPrivilages(resourcePrivilages.toArray(new String[resourcePrivilages.size()]));
								return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
							}
							else if(admin_details.get(0).getA_admin_level() == 2){
								List<String> resourcePrivilages = this.auth.getResourceNames("admin");
								privilages.setAdminPrivilages(resourcePrivilages.toArray(new String[resourcePrivilages.size()]));
								return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
							}
							else{
								log.warn("Unauthorized data in login function");
								exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
								return exception.getException("your admin level is not valid", null);
							}
						}
					}
					else{
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("give valid JsonData/keys", null);
					}					
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to getLoginDetails function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}				
			}else {
				log.warn("Unauthorized user tried to access getLoginDetails function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
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
				log.warn("Connection refused server stopped in login function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}
	}

	private int hasAccessControl(String apikey, String resourceName,String method, String functionName) {
		// TODO Auto-generated method stub
		String methodName = "";
		if(method.equalsIgnoreCase("get"))
			methodName = "a_have_get_permission";
		else if(method.equalsIgnoreCase("put"))
			methodName = "a_have_put_permission";
		else if(method.equalsIgnoreCase("post"))
			methodName = "a_have_post_permission";
		String userName = this.auth.getUserNameRelatedToAccessToken(apikey);
		long userId = this.auth.getUserId(userName, "mk_api_user_admin");
		long resourceId = this.auth.getResourceId(resourceName);
		if(isAdminActive(apikey)){
			if(this.auth.hasAccessToThisResource(userId,resourceId, "mk_api_resources_admin_permission", "a_admin_id") != 0){
				if(this.auth.isMethodPermissionExists(resourceId, userId, "mk_api_resources_admin_permission", "a_admin_id", methodName) == 1){
					if(this.auth.hasAccessToThisFunction(userId,this.auth.getFunctionId(resourceId, method.toLowerCase(), functionName),"a_admin_id","mk_api_resources_admin_function_permission") == 1){
						return 1;
					}else{
						return -3;
					}
				}else{
					return -2;
				}			
			}else{
				return -1;
			}
		}else{
			return 0;
		}
	}
	
	private String accessControlMessage(int response){
		String message = "";
		if(response == -1){
			message = "You don't have access to this API";
		}else if(response == -2){
			message = "You don't have access to this method";
		}else if(response == -3){
			message = "You don't have access to this function";
		}else if(response == 0){
			message = "you admin active state is revoked";
		}
		return message;
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
				int accessControl = hasAccessControl(apikey,"admin","post","registerUser");
				if(accessControl == 1){
					JSONObject rawData = helper.contextRequestParser(request);
					String generateduserAccessToken = helper.generateUniqueAccessToken();
					String generatedpassword = helper.generateRandomPassword();
					rawData.put("accessToken",generateduserAccessToken);
					rawData.put("password", generatedpassword);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
						int rowId = registerAdmin(rawData,adminUserName);
						if (rowId > 0) {
							int status = assigningPermission(rawData, rowId);
							//1 on successfull insertion and 6 on successfull updation
							if(status == 1 || status == 6){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" and also has given resource persmissions");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}else if(status == -2){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given persmissions to some of the resources");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions to some of the resources", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}else{
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given resource persmissions");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}
						} else if(rowId == 0) {
							log.warn("Invalid keys in registerUser function for admin");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("check your keys once again", null);
						} else if(rowId == -1){
							log.warn("Invalid email provided in registerUser function for admin");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("please provide valid email", null);
						} else if (rowId == -2){
							log.warn("Conflict, User name already exists in registerUser function for admin");
							exception = new HandleException(Response.Status.FORBIDDEN.getStatusCode(),Response.Status.FORBIDDEN.getReasonPhrase());
							return exception.getException("user name already exist", null);
						}else{
							log.warn("UnKnown Exception in registerUser function for admin");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("UnKnown Exception", null);
						}
					} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
						int rowId = registerConsumer(rawData,adminUserName);
						if (rowId > 0) {
							int status = assigningPermission(rawData, rowId);
							if(status == 1){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" and also has given resource persmissions");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
							}else if(status == -1){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given resource persmissions because you can't give consumer access to ADMIN resource and so avoided this permission");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned, can't give consumer access to ADMIN resource", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}else if(status == -2){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given persmissions to some of the resources");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions to some of the resources", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}else if(status == 7){
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given persmissions, give valid function keys");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions, give valid function keys ", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
							}else{
								log.info(adminUserName+" has registered "+(String)rawData.get("userName")+" but not given any resource persmissions");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
							}
						} else if(rowId == 0) {
							log.warn("Invalid keys in registerUser function for consumer");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("check your keys once again", null);
						} else if(rowId == -1){
							log.warn("Invalid email provided in registerUser function for consumer");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("please provide valid email", null);
						} else if (rowId == -2){
							log.warn("Conflict, User name already exists in registerUser function for consumer");
							exception = new HandleException(Response.Status.FORBIDDEN.getStatusCode(),Response.Status.FORBIDDEN.getReasonPhrase());
							return exception.getException("user name already exist", null);
						}else{
							log.warn("UnKnown Exception in registerUser function for consumer");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("UnKnown Exception", null);
						}
					} else {
						log.warn("Unauthorized data in registerUser function");
						exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
						return exception.getException("please provide valid details also check your adminLevel/activeState", null);
					}
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to registerUser function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			} else {
				log.warn("Unauthorized data in registerUser function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}				
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in registerUser function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
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
	public Object updatePermissions(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","put","updatePermissions");
				if(accessControl == 1 ){
					JSONObject rawData = helper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if(isUserSuperAdmin(((String) rawData.get("userName")))){
						log.warn("Trying to update super admin permissions in updatePermissions function");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("you can't update super admin permissions ", null);
					}else if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
						if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
							int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_user_admin");
							int status = assigningPermission(rawData, rowId);
							if(status == 1){
								//update consumer/admin table addedBy coloumn with adminUserName
								log.info(adminUserName+" has given "+(String)rawData.get("userName")+" resource persmissions");
								this.auth.modifiedBy("mk_api_user_admin",adminUserName,(long)rowId);
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
							}else if(status == 6){
								//update consumer/admin table addedBy coloumn with adminUserName
								log.info(adminUserName+" has updated "+(String)rawData.get("userName")+" resource persmissions");
								this.auth.modifiedBy("mk_api_user_admin",adminUserName,(long)rowId);
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
							}else
								return helper.checkStatus(status);
						}
						else{
							log.warn("Not found data in updatePermissions function for admin");
							exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
							return exception.getException("give valid user name", null);
						}
					} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) ) {
						if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
							int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_consumer");
							int status = assigningPermission(rawData, rowId);
							if(status == 1){
								//update consumer/admin table addedBy coloumn with adminUserName
								log.info(adminUserName+" has given "+(String)rawData.get("userName")+" resource persmissions");
								this.auth.modifiedBy("mk_api_consumer",adminUserName,(long)rowId);
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
							}else if(status == 6){
								//update consumer/admin table addedBy coloumn with adminUserName
								log.info(adminUserName+" has updated "+(String)rawData.get("userName")+" resource persmissions");
								this.auth.modifiedBy("mk_api_consumer",adminUserName,(long)rowId);
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
							}
							else
								return helper.checkStatus(status);
						}
						else{
							log.warn("Not found data in updatePermissions function for consumer");
							exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
							return exception.getException("give valid user name", null);
						}
					}else {
						log.warn("Unauthorized data in updatePermissions function");
						exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
						return exception.getException("please provide valid details also check your adminLevel/activeState", null);
					}
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to updatePermissions function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			}else {
				log.warn("Unauthorized data in updatePermissions function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in updatePermissions function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				e.printStackTrace();
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}			
	}
	
	/**
	 * This method checks whether the user we want to edit/change/update is super admin or not
	 * @param userName
	 * @return
	 */
	private boolean isUserSuperAdmin(String userName) {
		if(this.auth.isUserSuperAdmin(userName) == 1)
			return true;
		else
			return false;
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
	public Object changeUserActiveStatus(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request){
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","put","changeUserActiveStatus");
				if(accessControl == 1){
					JSONObject rawData = helper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if(isUserSuperAdmin(((String) rawData.get("userName")))){
						log.warn("Trying to update super admin active status in changeUserActiveStatus function");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("you can't update super admin active status ", null);
					}else if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isValidStatus((long) rawData.get("status"))) {
						if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
							this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_user_admin",adminUserName);
							this.auth.updateUserChanges("mk_api_user_admin",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_user_admin"),1);
							log.info(adminUserName+" has changed isActive status of user "+(String)rawData.get("userName"));
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}
						else{
							log.warn("Not found data in changeUserActiveStatus function for admin");
							exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
							return exception.getException("give valid user name", null);
						}
					} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isValidStatus((long) rawData.get("status"))) {
						if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
							this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_consumer",adminUserName);
							this.auth.updateUserChanges("mk_api_consumer",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_consumer"),1);
							log.info(adminUserName+" has changed isActive status of user "+(String)rawData.get("userName"));
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
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to changeUserActiveStatus function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			}else {
				log.warn("Unauthorized data in changeUserActiveStatus function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in changeUserActiveStatus function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}	
	}
	
	private boolean isValidStatus(long status) {
		if(status == 1 || status == 0)
			return true;
		else
			return false;
	}

	/**
	 * This is the Put method accessed by path HOST/v1.0/admin/changeRateLimit
	 * This method used to change user's countAssigned in DB
	 * @param apikey
	 * @param request
	 * @return
	 */
	@PUT
	@Path("/changeRateLimit")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object changeRateLimit(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request){
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","put","changeRateLimit");
				if(accessControl == 1){					
					JSONObject rawData = helper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
						this.auth.changeRateLimit((String) rawData.get("userName"),(long) rawData.get("rateLimit"),adminUserName);
						this.auth.updateUserChanges("mk_api_consumer",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_consumer"),1);
						log.info(adminUserName+" has changed countAssigned of user "+(String)rawData.get("userName"));
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
					}else{
						log.warn("Not found userName in changeRateLimit function");
						exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
						return exception.getException("give valid user name", null);
					}
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to changeRateLimit function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			}else {
				log.warn("Unauthorized data in changeRateLimit function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in changeRateLimit function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
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
				int accessControl = hasAccessControl(apikey,"admin","get","getUsersStatus");
				if(accessControl == 1){	
					JSONObject rawData = helper.jsonParser(userDetails);
					if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
						if(rawData.containsKey("status")){
							List<UserStatus> userStatusDetails = this.auth.getUsersDetailsWithStatus((long) rawData.get("status"),"mk_api_user_admin");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),userStatusDetails);
						}
						else{
							List<UserStatus> allUsersStatusDetails = this.auth.getUsersDetailsWithoutStatus("mk_api_user_admin");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase()+". Retrieved all admin status details", allUsersStatusDetails);
						}
					} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
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
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to getUsersStatus function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			}else {
				log.warn("Unauthorized data in getUsersStatus function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your accessToken/accessParam are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getUsersStatus function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}	
	}
	
	/**
	 * This is the Get method accessed by path HOST/v1.0/admin/getUserPrivileges
	 * It gives userName and privilages of every user
	 * @param apikey
	 * @param userDetails
	 * @return
	 */
	@GET
	@Path("/getUserPrivileges")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getUserPrivileges(@HeaderParam("accessToken") String apikey,@HeaderParam("accessParam") String userDetails){
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if((accessLevel == 1 || accessLevel == 2) && helper.isUserDetailsValidJson(userDetails)){
				int accessControl = hasAccessControl(apikey,"admin","get","getUserPrivileges");
				if(accessControl == 1){
					JSONObject rawData = helper.jsonParser(userDetails);
					if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
						if(rawData.containsKey("userName") && isUserNameAlreadyExists("admin",(String)rawData.get("userName"))){
							int adminId = this.auth.getUserId((String)rawData.get("userName"), "mk_api_user_admin");
							List<Privilages> userDetail = this.auth.getUserPrivileges((long) adminId,"mk_api_resources_admin_permission","a_admin_id");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),new UserPrivilagesResponse((String)rawData.get("userName"),userDetail));
						}
						else{
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("give valid keys", null);
						}
					} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) ) {
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
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to getUserPrivileges function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			}else {
				log.warn("Unauthorized data in getUserPrivileges function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your accessToken/accessParam are not acceptable", null);
			}
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getUserPrivileges function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}
	}
	
	@POST
	@Path("/addNewResource")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addNewResource(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request) {
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","post","addNewResource");
				if(accessControl == 1){
					JSONObject rawData = helper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if(rawData.containsKey("resourceNames")){
						JSONArray resources = (JSONArray) rawData.get("resourceNames");
						if(resources.size() > 0){
							List<String> preAssignedResourceNames = this.auth.getResourceNames("null");
							for (int i = 0; i < resources.size(); i++) {
								if(!preAssignedResourceNames.contains((String)resources.get(i))){
									if((String)resources.get(i) == null || ((String)resources.get(i)).equals("")){
									}else
										this.auth.addNewResource((String)resources.get(i));
								}
							}
							log.info(adminUserName + " has added new resource");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else{
							log.warn(adminUserName + " tried to access addNewResource without providing any resources names");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("provide resource names", null);
						}
					}else{
						log.warn(adminUserName + " tried to access addNewResource without giving valid key");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("give valid keys", null);
					}
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to addNewResource function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			} else {
				log.warn("Unauthorized data in addNewResource function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}				
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in addNewResource function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
			}else{
				log.warn(e.getMessage());
				exception = new HandleException(Response.Status.EXPECTATION_FAILED.getStatusCode(),Response.Status.EXPECTATION_FAILED.getReasonPhrase());
				return exception.getException("unknown exception caused", null);
			}
		}				
	}

	
	@POST
	@Path("/addNewFunction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addNewFunction(@HeaderParam("accessToken") String apikey,@Context HttpServletRequest request) {
		int errors = 0;
		try{
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				int accessControl = hasAccessControl(apikey,"admin","post","addNewFunction");
				if(accessControl == 1){
					JSONObject rawData = helper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					if(rawData.containsKey("addFunctions")){
						JSONArray resourceNamesWithFunctions = (JSONArray) rawData.get("addFunctions");
						if(resourceNamesWithFunctions.size() > 0){
							//This will return all resource names excluding the admin resource
							List<String> preAssignedResourceNames = this.auth.getResourceNames("admin");
							for (int i = 0; i < resourceNamesWithFunctions.size(); i++) {
								JSONObject functionObject = (JSONObject) resourceNamesWithFunctions.get(i);
								if(functionObject.containsKey("resourceName") && preAssignedResourceNames.contains((String)functionObject.get("resourceName"))){
									long resourceId = this.auth.getResourceId((String)functionObject.get("resourceName"));
									if(functionObject.containsKey("getFunctions")){
										JSONArray getFunctions = (JSONArray) functionObject.get("getFunctions");
										List<String> getPreAssignedGETFunctions = this.auth.getFunctionNames(resourceId, "get");
										for (int j = 0; j < getFunctions.size(); j++) {
											if(!getPreAssignedGETFunctions.contains(getFunctions.get(j))){
												if(getFunctions.get(j) == null || (getFunctions.get(j)).equals("")){
												}else
													this.auth.insertNewFunction(resourceId,(String)getFunctions.get(j),"get");
											}
										}
									}else
									if(functionObject.containsKey("postFunction")){
										JSONArray postFunctions = (JSONArray) functionObject.get("postFunctions");
										List<String> getPreAssignedPOSTFunctions = this.auth.getFunctionNames(resourceId, "post");
										for (int j = 0; j < postFunctions.size(); j++) {
											if(!getPreAssignedPOSTFunctions.contains(postFunctions.get(j))){
												if(postFunctions.get(j) == null || (postFunctions.get(j)).equals("")){
												}else
													this.auth.insertNewFunction(resourceId,(String)postFunctions.get(j),"post");
											}
										}
									}
									if(functionObject.containsKey("putFunction")){
										JSONArray putFunctions = (JSONArray) functionObject.get("putFunctions");
										List<String> getPreAssignedPUTFunctions = this.auth.getFunctionNames(resourceId, "put");
										for (int j = 0; j < putFunctions.size(); j++) {
											if(!getPreAssignedPUTFunctions.contains(putFunctions.get(j))){
												if(putFunctions.get(j) == null || (putFunctions.get(j)).equals("")){
												}else
													this.auth.insertNewFunction(resourceId,(String)putFunctions.get(j),"put");
											}
										}
									}
								}else{
									errors++;
								}
							}
							if(errors == 0){
								log.info(adminUserName + " has added new functions");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
							}else{
								log.info(adminUserName + " has added only some functions, there were errors in the requested json");
								return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but added only some functions, there were errors in the requested json",null);
							}
						}else{
							log.warn(adminUserName + " tried to access addNewFunction without providing any function names specified");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("provide resource names", null);
						}
					}else{
						log.warn(adminUserName + " tried to access addNewFunction without giving valid key");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("give valid keys", null);
					}
				}else{
					String message = accessControlMessage(accessControl);
					log.warn("Access control denied to addNewFunction function");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException(message, null);
				}	
			} else {
				log.warn("Unauthorized data in addNewFunction function");
				exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
				return exception.getException("your access token is not acceptable", null);
			}				
		}catch(NullPointerException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid keys", null);
		}catch(ClassCastException e){
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("give valid values", null);
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in addNewFunction function");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Connection refused server stopped", null);
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
	private int registerConsumer(JSONObject jsonData,String addedBy) {
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("countAssigned")) {
			if(helper.emailIsValid((String) jsonData.get("userName"))){
				if(!isUserNameAlreadyExists("consumer",(String) jsonData.get("userName"))){
					int id = this.auth.addConsumer((String) jsonData.get("userName"),(String) jsonData.get("accessToken"),(long) jsonData.get("countAssigned"),addedBy);
					return id;
				}else{
					return -2;
				}			
			}else{
				return -1;
			}			
		} else
			return 0;
	}
	
	/**
	 * This method registers new Admins
	 * @param jsonData of admin details
	 * @return int newly inserted row ID
	 */
	public int registerAdmin(JSONObject jsonData,String addedBy){
		if (jsonData.containsKey("userName") && jsonData.containsKey("accessToken") && jsonData.containsKey("password")) {
			if(helper.emailIsValid((String) jsonData.get("userName"))){
				if(!isUserNameAlreadyExists("admin",(String) jsonData.get("userName"))){
					int id = this.auth.addAdmin((String) jsonData.get("userName"),MD5Encoding.encrypt((String) jsonData.get("password")),(String) jsonData.get("accessToken"),addedBy);
					return id;					
				}else{
					return -2;
				}				
			}else{
				return -1;
			}
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
		JSONArray getFunctionJsonArray = null;
		JSONArray postFunctionJsonArray = null;
		JSONArray putFunctionJsonArray = null;
		List<String> getFunctionName = new ArrayList<String>();
		List<String> putFunctionName = new ArrayList<String>();
		List<String> postFunctionName = new ArrayList<String>();
		try{
			permissionJsonArray = (JSONArray) permissions.get("permission");
			if(to.equals("consumer")){
				boolean hasFunctions = false;
				if(permissions.containsKey("getFunctions")){
					getFunctionJsonArray = (JSONArray) permissions.get("getFunctions");
					hasFunctions  = true;
				}
				if(permissions.containsKey("postFunctions")){
					postFunctionJsonArray = (JSONArray) permissions.get("postFunctions");
					hasFunctions  = true;
				}
				if(permissions.containsKey("putFunctions")){
					putFunctionJsonArray = (JSONArray) permissions.get("putFunctions");
					hasFunctions  = true;
				}
				if(!hasFunctions)
					return 7;
			}
		}catch(ClassCastException e){
			//Refer to checkstatus method in HelperMethods class
			return 4;
		}
		for (int j = 0; j < permissionJsonArray.size(); j++) {
			if (((String) permissionJsonArray.get(j)).toUpperCase().equals("GET")) {
				get = 1;
				getFunctionName.addAll(this.auth.getFunctionNames(resourceId,"get"));
			} else if (((String) permissionJsonArray.get(j)).toUpperCase().equals("POST")) {
				post = 1;
				postFunctionName.addAll(this.auth.getFunctionNames(resourceId,"post"));
			} else if (((String) permissionJsonArray.get(j)).toUpperCase().equals("PUT")) {
				put = 1;
				putFunctionName.addAll(this.auth.getFunctionNames(resourceId,"put"));
			} 
			else {
				//Refer to checkstatus method in HelperMethods class
				if (((String) permissionJsonArray.get(j)).toUpperCase().equals("GET") || ((String) permissionJsonArray.get(j)).toUpperCase().equals("POST") || ((String) permissionJsonArray.get(j)).toUpperCase().equals("PUT")) {
					
				} else{
					return 5;
				}				
			}
		}
		int typeOfQuery = 0;
		if(type.equals("insert")){
			System.out.println("In Insert Query");
			if(to.equals("consumer")){
				System.out.println("User type Consumer");				
				if(get == 1)
					try{
						for(int i = 0; i < getFunctionJsonArray.size(); i++)
							if(getFunctionName.contains((String) getFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"get",((String) getFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
					}catch(Exception e){
						System.out.println("Exception in get function");
					}
				if(post == 1)
					try{
						for(int i = 0; i < postFunctionJsonArray.size(); i++)
							if(postFunctionName.contains((String) postFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"post",((String) postFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
					}catch(Exception e){
						System.out.println("Exception in post function");
					}
				if(put == 1)
					try{
						for(int i = 0; i < putFunctionJsonArray.size(); i++)
							if(putFunctionName.contains((String) putFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"put",((String) putFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
					}catch(Exception e){
						System.out.println("Exception in put function");
					}
				this.auth.insertUserPermission(resourceId,userId, get, post, put,"mk_api_resources_consumer_permission","a_consumer_id");
			}else{
				System.out.println("User type Admin");				
				for (int j = 0; j < getFunctionName.size(); j++)
					this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"get",((String) getFunctionName.get(j))),1,"mk_api_resources_admin_function_permission","a_admin_id");
				for (int j = 0; j < postFunctionName.size(); j++)
					this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"post",((String) postFunctionName.get(j))),1,"mk_api_resources_admin_function_permission","a_admin_id");
				for (int j = 0; j < putFunctionName.size(); j++)
					this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"put",((String) putFunctionName.get(j))),1,"mk_api_resources_admin_function_permission","a_admin_id");
				this.auth.insertUserPermission(resourceId,userId, get, post, put,"mk_api_resources_admin_permission","a_admin_id");
			}
			//Refer to checkstatus method in HelperMethods class
			typeOfQuery = 1;
		}else if(type.equals("update")){
			System.out.println("In Update Query");
			if(to.equals("consumer")){
				System.out.println("User type Consumer");
				try{
					if(get == 1){
						List<String> getPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"get",1,"a_consumer_id","mk_api_resources_consumer_function_permission");
						List<String> getDeactivatedPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"get",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
						for(int i = 0; i < getFunctionJsonArray.size(); i++)
							if(getPreAssignedFunctionNames.contains((String) getFunctionJsonArray.get(i)))
								getPreAssignedFunctionNames.remove((String) getFunctionJsonArray.get(i));
							else if(getFunctionName.contains((String) getFunctionJsonArray.get(i)) && !getDeactivatedPreAssignedFunctionNames.contains((String) getFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"get",((String) getFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
							else
								this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"get",((String) getFunctionJsonArray.get(i))), userId, 1, "a_consumer_id", "mk_api_resources_consumer_function_permission");
						for(int i = 0; i < getPreAssignedFunctionNames.size(); i++)
							this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"get",getPreAssignedFunctionNames.get(i)),userId,0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}else{
						// Remove all get function permissions for this user specific to this resource id
						this.auth.removeFunctionPermissions(resourceId,userId,"get",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}
				}catch(Exception e){
					System.out.println("Exception in update get function");
				}				
				try{
					if(post == 1){
						List<String> getPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"post",1,"a_consumer_id","mk_api_resources_consumer_function_permission");
						List<String> getDeactivatedPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"post",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
						for(int i = 0; i < postFunctionJsonArray.size(); i++)
							if(getPreAssignedFunctionNames.contains((String) postFunctionJsonArray.get(i)))
								getPreAssignedFunctionNames.remove((String) postFunctionJsonArray.get(i));
							else if(postFunctionName.contains((String) postFunctionJsonArray.get(i)) && !getDeactivatedPreAssignedFunctionNames.contains((String) postFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"post",((String) postFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
							else
								this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"post",((String) postFunctionJsonArray.get(i))), userId, 1, "a_consumer_id", "mk_api_resources_consumer_function_permission");
						for(int i = 0; i < getPreAssignedFunctionNames.size(); i++)
							this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"post",getPreAssignedFunctionNames.get(i)),userId,0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}else{
						// Remove all get function permissions for this user specific to this resource id
						this.auth.removeFunctionPermissions(resourceId,userId,"post",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}
				}catch(Exception e){
					System.out.println("Exception in update post function");
				}				
				try{
					if(put == 1){
						List<String> getPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"put",1,"a_consumer_id","mk_api_resources_consumer_function_permission");
						List<String> getDeactivatedPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,"put",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
						for(int i = 0; i < putFunctionJsonArray.size(); i++)
							if(getPreAssignedFunctionNames.contains((String) putFunctionJsonArray.get(i)))
								getPreAssignedFunctionNames.remove((String) putFunctionJsonArray.get(i));
							else if(putFunctionName.contains((String) putFunctionJsonArray.get(i)) && !getDeactivatedPreAssignedFunctionNames.contains((String) putFunctionJsonArray.get(i)))
								this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,"put",((String) putFunctionJsonArray.get(i))),1,"mk_api_resources_consumer_function_permission","a_consumer_id");
							else
								this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"put",((String) putFunctionJsonArray.get(i))), userId, 1, "a_consumer_id", "mk_api_resources_consumer_function_permission");
						for(int i = 0; i < getPreAssignedFunctionNames.size(); i++)
							this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,"put",getPreAssignedFunctionNames.get(i)),userId,0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}else{
						// Remove all get function permissions for this user specific to this resource id
						this.auth.removeFunctionPermissions(resourceId,userId,"put",0,"a_consumer_id","mk_api_resources_consumer_function_permission");
					}
				}catch(Exception e){
					System.out.println("Exception in update put function");
				}
				this.auth.updateUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_consumer_permission","a_consumer_id");
			}else{	
				System.out.println("User type Admin");
				if(get == 0){
					System.out.println("Removing get permissions");
					this.auth.removeFunctionPermissions(resourceId,userId,"get",0,"a_admin_id","mk_api_resources_admin_function_permission");
				}else{
					System.out.println("Giving get permissions");
					updateAdminMethodPermission(resourceId,userId,"get",getFunctionName);
				}
				if(post == 0){
					System.out.println("Removing post permissions");
					this.auth.removeFunctionPermissions(resourceId,userId,"post",0,"a_admin_id","mk_api_resources_admin_function_permission");
				}else{
					System.out.println("Updating post permissions");
					updateAdminMethodPermission(resourceId,userId,"post",postFunctionName);
				}
				if(put == 0){
					System.out.println("Removing put permissions");
					this.auth.removeFunctionPermissions(resourceId,userId,"put",0,"a_admin_id","mk_api_resources_admin_function_permission");
				}else{
					System.out.println("Updating put permissions");
					updateAdminMethodPermission(resourceId,userId,"put",putFunctionName);
				}
				this.auth.updateUserPermission(resourceId,userId, get, post, put, delete,"mk_api_resources_admin_permission","a_admin_id");
			}
			//Refer to checkstatus method in HelperMethods class
			typeOfQuery = 6;
		}
		return typeOfQuery;		
	}
	
	/**
	 * This method updates admin method and functions permissions  
	 * @param resourceId
	 * @param userId
	 * @param method
	 * @param functionNames
	 */
	private void updateAdminMethodPermission(long resourceId,long userId,String method,List<String> functionNames){
		List<String> getPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,method,1,"a_admin_id","mk_api_resources_admin_function_permission");
		List<String> getDeactivatedPreAssignedFunctionNames = this.auth.getPreAssignedFunctionNames(resourceId,userId,method,0,"a_admin_id","mk_api_resources_admin_function_permission");						
		for(int i = 0; i < getPreAssignedFunctionNames.size(); i++)
			if(functionNames.contains(getPreAssignedFunctionNames.get(i)))
				functionNames.remove(getPreAssignedFunctionNames.get(i));
			else
				this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,method,getPreAssignedFunctionNames.get(i)),userId,0,"a_admin_id","mk_api_resources_admin_function_permission");
		for(int i = 0; i < functionNames.size(); i++)
			if(getDeactivatedPreAssignedFunctionNames.contains(functionNames.get(i)))
				this.auth.updateSpecificFunctionNames(this.auth.getFunctionId(resourceId,method,functionNames.get(i)),userId,1,"a_admin_id","mk_api_resources_admin_function_permission");
			else
				this.auth.insertUserFunctionPermissions(userId,this.auth.getFunctionId(resourceId,method,((String) functionNames.get(i))),1,"mk_api_resources_admin_function_permission","a_admin_id");		
	}

	/**
	 * This method assigns resource permissions to the consumers/admin
	 * @param jsonData of permissions details 
	 * @param userId contains row ID's of consumer/admin
	 * @return int
	 */
	private int assigningPermission(JSONObject jsonData, int userId) {
		int result_status = 0;
		int numberOfSuccessfullResults = 0; 
		long adminResourceId = ((long)this.auth.getResourceId("admin"));
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
						if(adminResourceId != resourceId){			
							if(isPermissionExists((String)jsonData.get("type"),resourceId,(long)userId)){
								result_status = givePermission((String)jsonData.get("type"),"update",resourceId,(long)userId,permissions);
								//Refer to checkstatus method in HelperMethods class
								if(result_status == 4 || result_status == 5 || result_status == 7)
									return result_status;
								else{
									if(((String)jsonData.get("type")).equals("admin")){
										this.auth.updateUserChanges("mk_api_user_admin",(long)userId,1);								
									}else{
										this.auth.updateUserChanges("mk_api_consumer",(long)userId,1);									
									}
									numberOfSuccessfullResults++;
								}
							}
							else{
								result_status = givePermission((String)jsonData.get("type"),"insert",resourceId,(long)userId,permissions);
								//Refer to checkstatus method in HelperMethods class
								if(result_status == 4 || result_status == 5 || result_status == 7)
									return result_status;
								numberOfSuccessfullResults++;
							}
						}
						else if(adminResourceId == resourceId && ((String)jsonData.get("type")).equals("admin")){
							if(isPermissionExists((String)jsonData.get("type"),resourceId,(long)userId)){
								result_status = givePermission((String)jsonData.get("type"),"update",resourceId,(long)userId,permissions);
								//Refer to checkstatus method in HelperMethods class
								if(result_status == 4 || result_status == 5 || result_status == 7)
									return result_status;
								else{
									this.auth.updateUserChanges("mk_api_user_admin",(long)userId,1);
									numberOfSuccessfullResults++;
								}
							}
							else{
								result_status = givePermission((String)jsonData.get("type"),"insert",resourceId,(long)userId,permissions);
								//Refer to checkstatus method in HelperMethods class
								if(result_status == 4 || result_status == 5 || result_status == 7)
									return result_status;
								numberOfSuccessfullResults++;
							}
						}
						else{
							result_status = -1;
						}
					} else {
						//Refer to checkstatus method in HelperMethods class
						return 3;
					}
				}
				if(((String)jsonData.get("type")).equals("admin")){
					this.auth.dateModified("mk_api_user_admin",helper.currentTimeStamp(),(long)userId );								
				}else{
					this.auth.dateModified("mk_api_consumer",helper.currentTimeStamp(),(long)userId );									
				}
				if(numberOfSuccessfullResults == resources.size())
					return 1;
				else if(numberOfSuccessfullResults < resources.size() && numberOfSuccessfullResults != 0)
					return -2;
				else
					return -1;
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
