package com.mebelkart.api.admin.v1.resources;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.admin.v1.core.Admin;
import com.mebelkart.api.admin.v1.core.Privilages;
import com.mebelkart.api.admin.v1.core.UserStatus;
import com.mebelkart.api.util.crypting.MD5Encoding;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

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
	//HandleException exception = new HandleException();
	
	/**
	 * local helpers
	 */
	HelperMethods helper = new HelperMethods();
	/**
	 * global helpers
	 */
	Helper utilHelper = new Helper();
	
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();
	
	/**
	 * InvalidInputReplyClass class
	 */
	InvalidInputReplyClass invalidRequestReply = null;
	
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
			/*
			 * decoding encoded apikey given by the admin
			 */
			apikey = helper.getBase64Decoded(apikey);
			userDetails = helper.getBase64Decoded(userDetails);
			int accessLevel = this.auth.validate(apikey);			
			//Here 1 is Super Admin and 2 is Secondary Admin and this is the accessToken which is passed through interface while logging
			if(accessLevel == 1 || accessLevel == 2){
				if(utilHelper.isValidJson(userDetails) && helper.isUserDetailsContainsValidKeys(userDetails)){
					JSONObject userDetailsObj = utilHelper.jsonParser(userDetails);
					String username = (String) userDetailsObj.get("userName");
					String password = MD5Encoding.encrypt((String) userDetailsObj.get("password"));
					List<Admin> adminDetails = this.auth.login(username, password);
					if (!adminDetails.get(0).getUserName().equals(username)) {
						log.warn("Invalid username or password");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "Invalid username or password");
						return invalidRequestReply;
					} else {
						String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
						try {
							authenticate.validate(adminUserName,apikey, "admin", "get", "login");
						} catch (Exception e) {					
							log.info("Unautherized user "+username+" tried to access admin function");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
							return invalidRequestReply;
						}						
						AdminPrivilagesResponse privilages = new AdminPrivilagesResponse();
						if(adminDetails.get(0).getAdminLevel() == 1){
							// Here parameter null means I need all the resource names 
							List<String> resourcePrivilages = this.auth.getResourceNames("null");
							privilages.setSuperAdminPrivilages(resourcePrivilages.toArray(new String[resourcePrivilages.size()]));
							String sessionId = helper.generateSessionId();
							privilages.setSessionId(sessionId);
							privilages.setUserLevel("1");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
						}
						else if(adminDetails.get(0).getAdminLevel() == 2){
							// Here parameter admin means I need all the resource names except admin
							List<String> resourcePrivilages = this.auth.getResourceNames("admin");
							privilages.setAdminPrivilages(resourcePrivilages.toArray(new String[resourcePrivilages.size()]));
							String sessionId = helper.generateSessionId();
							privilages.setSessionId(sessionId);
							privilages.setUserLevel("2");
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), privilages);
						}
						else{
							log.warn("Invalid adminLevel in login function");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "Your admin level is not valid");
							return invalidRequestReply;
						}
					}
				}
				else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid JsonData/keys");
					return invalidRequestReply;
				}
			}
			else {
				log.warn("Unauthorized user tried to access login function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your access token is not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
		    invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(IndexOutOfBoundsException e){
			log.warn("unauthorized request in login function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"please provide valid details");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in login function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
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
	public Object registerUser(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request) {
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "post", "registerUser");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access registerUser function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				JSONObject rawData = utilHelper.contextRequestParser(request);
				// Here the access token is unique 32 bit String
				String generateduserAccessToken = helper.generateUniqueAccessToken();
				// Here the password is random Alpha numeric String of length 5
				String generatedpassword = helper.generateRandomPassword();
				rawData.put("accessToken",generateduserAccessToken);
				rawData.put("password", generatedpassword);
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
					int rowId = registerAdmin(rawData,userName);
					if (rowId > 0) {
						int status = assigningPermission(rawData, rowId);
						//1 on successfull insertion and 6 on successfull updation
						if(status == 1 || status == 6){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" and also has given resource persmissions");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}else if(status == -2){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given persmissions to some of the resources");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions to some of the resources", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}else{
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given resource persmissions");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}
					} else if(rowId == 0) {
						log.warn("Invalid keys in registerUser function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"check your keys once again");
						return invalidRequestReply;
					} else if(rowId == -1){
						log.warn("Invalid email provided in registerUser function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"please provide valid email");
						return invalidRequestReply;
					} else if (rowId == -2){
						log.warn("Conflict, User name already exists in registerUser function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.FORBIDDEN.getStatusCode(), Response.Status.FORBIDDEN.getReasonPhrase(),"user name already exist");
						return invalidRequestReply;
					}else{
						log.warn("UnKnown Exception in registerUser function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"UnKnown Exception");
						return invalidRequestReply;
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2)) {
					int rowId = registerConsumer(rawData,userName);
					if (rowId > 0) {
						int status = assigningPermission(rawData, rowId);
						if(status == 1){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" and also has given resource persmissions");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
						}else if(status == -1){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given resource persmissions because you can't give consumer access to ADMIN resource and so avoided this permission");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned, can't give consumer access to ADMIN resource", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}else if(status == -2){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given persmissions to some of the resources");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions to some of the resources", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}else if(status == 7){
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given persmissions, give valid function keys");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but not given persmissions, give valid function keys ", new AdminResponse(generateduserAccessToken,generatedpassword,(String)rawData.get("userName")));
						}else{
							log.info(userName+" has registered "+(String)rawData.get("userName")+" but not given any resource persmissions");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but permissions not assigned", new ConsumerResponse(generateduserAccessToken,(String)rawData.get("userName")));
						}
					} else if(rowId == 0) {
						log.warn("Invalid keys in registerUser function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"check your keys once again");
						return invalidRequestReply;
					} else if(rowId == -1){
						log.warn("Invalid email provided in registerUser function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"please provide valid email");
						return invalidRequestReply;
					} else if (rowId == -2){
						log.warn("Conflict, User name already exists in registerUser function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.FORBIDDEN.getStatusCode(), Response.Status.FORBIDDEN.getReasonPhrase(),"user name already exist");
						return invalidRequestReply;
					}else{
						log.warn("UnKnown Exception in registerUser function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"UnKnown Exception");
						return invalidRequestReply;
					}
				} else {
					log.warn("Unauthorized data in registerUser function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"please provide valid details also check your adminLevel/activeState");
					return invalidRequestReply;
				}
			} else {
				log.warn("Unauthorized data in registerUser function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your access token is not acceptable");
				return invalidRequestReply;
			}				
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in registerUser function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}				
	}

	private boolean isHavingValidAccessParamKeys(String accessParam) {
		JSONObject jsonData = utilHelper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken"))
			return true;
		else
			return false;
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
	public Object updatePermissions(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request) {
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = utilHelper.contextRequestParser(request);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "put", "updatePermissions");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access updatePermissions function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if(isUserSuperAdmin(((String) rawData.get("userName")))){
					log.warn("Trying to update super admin permissions in updatePermissions function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"you can't update super admin permissions");
					return invalidRequestReply;
				}else if(isUserInterfaceUser(((String) rawData.get("userName")))){
					log.warn("Trying to update interfcae user permissions in updatePermissions function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"you can't update interface user permissions");
					return invalidRequestReply;
				}else if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
					if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
						int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_user_admin");
						int status = assigningPermission(rawData, rowId);
						if(status == 1){
							//update consumer/admin table addedBy coloumn with adminUserName
							log.info(userName+" has given "+(String)rawData.get("userName")+" resource persmissions");
							this.auth.modifiedBy("mk_api_user_admin",userName,(long)rowId);
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else if(status == 6){
							//update consumer/admin table addedBy coloumn with adminUserName
							log.info(userName+" has updated "+(String)rawData.get("userName")+" resource persmissions");
							this.auth.modifiedBy("mk_api_user_admin",userName,(long)rowId);
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else
							return helper.checkStatus(status);
					}
					else{
						log.warn("Not found data in updatePermissions function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase(),"give valid user name");
						return invalidRequestReply;
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) ) {
					if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
						int rowId = this.auth.getUserId((String) rawData.get("userName"),"mk_api_consumer");
						int status = assigningPermission(rawData, rowId);
						if(status == 1){
							//update consumer/admin table addedBy coloumn with adminUserName
							log.info(userName+" has given "+(String)rawData.get("userName")+" resource persmissions");
							this.auth.modifiedBy("mk_api_consumer",userName,(long)rowId);
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else if(status == 6){
							//update consumer/admin table addedBy coloumn with adminUserName
							log.info(userName+" has updated "+(String)rawData.get("userName")+" resource persmissions");
							this.auth.modifiedBy("mk_api_consumer",userName,(long)rowId);
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}
						else
							return helper.checkStatus(status);
					}
					else{
						log.warn("Not found data in updatePermissions function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase(),"give valid user name");
						return invalidRequestReply;
					}
				}else {
					log.warn("Unauthorized data in updatePermissions function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"please provide valid details also check your adminLevel/activeState");
					return invalidRequestReply;
				}	
			}else {
				log.warn("Unauthorized data in updatePermissions function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your access token is not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in updatePermissions function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				e.printStackTrace();
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}			
	}
	
	private boolean isUserInterfaceUser(String userName) {
		if(this.auth.isUserInterfaceUser(userName) == 0)
			return true;
		else
			return false;
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
	public Object changeUserActiveStatus(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request){
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = utilHelper.contextRequestParser(request);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "put", "changeUserActiveStatus");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access changeUserActiveStatus function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if(isUserSuperAdmin(((String) rawData.get("userName")))){
					log.warn("Trying to update super admin active status in changeUserActiveStatus function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"you can't update super admin active status");
					return invalidRequestReply;
				}else if(isUserInterfaceUser(((String) rawData.get("userName")))){
					log.warn("Trying to update interfcae user active status in changeUserActiveStatus function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"you can't update interface user active status");
					return invalidRequestReply;
				}else if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 && isValidStatus((long) rawData.get("status"))) {
					if(isUserNameAlreadyExists("admin",(String) rawData.get("userName"))){
						this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_user_admin",userName);
						if(((long) rawData.get("status")) == 1){
							this.auth.updateUserChanges("mk_api_user_admin",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_user_admin"),1);
						}
						log.info(userName+" has changed isActive status of user "+(String)rawData.get("userName"));
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
					}
					else{
						log.warn("Not found data in changeUserActiveStatus function for admin");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase(),"give valid user name");
						return invalidRequestReply;
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) && isValidStatus((long) rawData.get("status"))) {
					if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
						this.auth.changeUserActiveStatus((String) rawData.get("userName"),(long) rawData.get("status"),"mk_api_consumer",userName);
						if(((long) rawData.get("status")) == 1){
							this.auth.updateUserChanges("mk_api_consumer",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_consumer"),1);
						}
						log.info(userName+" has changed isActive status of user "+(String)rawData.get("userName"));
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
					}
					else{
						log.warn("Not found data in changeUserActiveStatus function for consumer");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase(),"give valid user name");
						return invalidRequestReply;
					}
				} else {
					log.warn("Unauthorized data in changeUserActiveStatus function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"please provide valid details also check your adminLevel/activeState");
					return invalidRequestReply;
				}
			}else {
				log.warn("Unauthorized data in changeUserActiveStatus function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your access token is not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in changeUserActiveStatus function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
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
	public Object changeRateLimit(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request){
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1 || accessLevel == 2){
				JSONObject rawData = utilHelper.contextRequestParser(request);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "put", "changeRateLimit");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access changeRateLimit function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if(isUserNameAlreadyExists("consumer",(String) rawData.get("userName"))){
					this.auth.changeRateLimit((String) rawData.get("userName"),(long) rawData.get("rateLimit"),userName);
					this.auth.updateUserChanges("mk_api_consumer",(long)this.auth.getUserId((String) rawData.get("userName"), "mk_api_consumer"),1);
					log.info(userName+" has changed countAssigned of user "+(String)rawData.get("userName"));
					return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
				}else{
					log.warn("Not found userName in changeRateLimit function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase(),"give valid user name");
					return invalidRequestReply;
				}	
			}else {
				log.warn("Unauthorized data in changeRateLimit function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "your access token is not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in changeRateLimit function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
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
	public Object getUsersStatus(@HeaderParam("userDetails") String accessParam,@HeaderParam("accessParam") String userDetails) {
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if((accessLevel == 1 || accessLevel == 2) && utilHelper.isValidJson(userDetails)){
				JSONObject rawData = utilHelper.jsonParser(userDetails);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "get", "getUsersStatus");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access getUsersStatus function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
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
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "please provide valid details also check your adminLevel/activeState");
					return invalidRequestReply;
				}	
			}else {
				log.warn("Unauthorized data in getUsersStatus function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "your accessLevel/accessParam are not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getUsersStatus function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(),"Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
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
	public Object getUserPrivileges(@HeaderParam("userDetails") String accessParam,@HeaderParam("accessParam") String userDetails){
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if((accessLevel == 1 || accessLevel == 2) && utilHelper.isValidJson(userDetails)){
				JSONObject rawData = utilHelper.jsonParser(userDetails);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "get", "getUserPrivileges");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access getUserPrivileges function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if (((String) rawData.get("type")).equals("admin") && accessLevel == 1 ) {
					if(rawData.containsKey("userName") && isUserNameAlreadyExists("admin",(String)rawData.get("userName"))){
						int adminId = this.auth.getUserId((String)rawData.get("userName"), "mk_api_user_admin");
						List<Privilages> userDetail = this.auth.getUserPrivileges((long) adminId,"mk_api_resources_admin_permission","a_admin_id");
						for(int i = 0; i < userDetail.size(); i++){
							if(userDetail.get(i).getGET() == 1){
								userDetail.get(i).setGetFunctions(this.auth.getFunctionNames((long) adminId,"mk_api_resources_admin_function_permission",userDetail.get(i).getResourceName(),"get","a_admin_id"));
							}
							if(userDetail.get(i).getPOST() == 1){
								userDetail.get(i).setPostFunctions(this.auth.getFunctionNames((long) adminId,"mk_api_resources_admin_function_permission",userDetail.get(i).getResourceName(),"post","a_admin_id"));
							}
							if(userDetail.get(i).getPUT() == 1){
								userDetail.get(i).setPutFunctions(this.auth.getFunctionNames((long) adminId,"mk_api_resources_admin_function_permission",userDetail.get(i).getResourceName(),"put","a_admin_id"));
							}
						}
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),new UserPrivilagesResponse((String)rawData.get("userName"),userDetail));
					}
					else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "give valid keys");
						return invalidRequestReply;
					}
				} else if (((String) rawData.get("type")).equals("consumer")&& (accessLevel == 1 || accessLevel == 2) ) {
					if(rawData.containsKey("userName") && isUserNameAlreadyExists("consumer",(String)rawData.get("userName"))){
						int consumerId = this.auth.getUserId((String)rawData.get("userName"), "mk_api_consumer");
						List<Privilages> userDetail = this.auth.getUserPrivileges((long) consumerId,"mk_api_resources_consumer_permission","a_consumer_id");
						for(int i = 0; i < userDetail.size(); i++){
							if(userDetail.get(i).getGET() == 1){
								userDetail.get(i).setGetFunctions(this.auth.getFunctionNames((long) consumerId,"mk_api_resources_consumer_function_permission",userDetail.get(i).getResourceName(),"get","a_consumer_id"));
							}
							if(userDetail.get(i).getPOST() == 1){
								userDetail.get(i).setPostFunctions(this.auth.getFunctionNames((long) consumerId,"mk_api_resources_consumer_function_permission",userDetail.get(i).getResourceName(),"post","a_consumer_id"));
							}
							if(userDetail.get(i).getPUT() == 1){
								userDetail.get(i).setPutFunctions(this.auth.getFunctionNames((long) consumerId,"mk_api_resources_consumer_function_permission",userDetail.get(i).getResourceName(),"put","a_consumer_id"));
							}
						}
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),new UserPrivilagesResponse((String)rawData.get("userName"),userDetail));
					}
					else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "give valid keys");
						return invalidRequestReply;
					}
				} else {
					log.warn("Unauthorized data in getUserPrivileges function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"please provide valid details also check your adminLevel/activeState");
					return invalidRequestReply;
				}
			}else {
				log.warn("Unauthorized data in getUserPrivileges function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "your accessToken/accessParam are not acceptable");
				return invalidRequestReply;
			}
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getUserPrivileges function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}
	}
	
	@GET
	@Path("/getFunctions")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getFunctions(@HeaderParam("userDetails") String accessParam,@HeaderParam("accessParam") String details){
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
			try {
				authenticate.validate(adminUserName,apikey, "admin", "get", "getFunctions");
			} catch (Exception e) {
				log.info("Unautherized user "+userName+" tried to access getFunctions function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
			}
			if(utilHelper.isValidJson(details)){
				JSONObject rawData = utilHelper.jsonParser(details);
				if(containsValidKeys(rawData)){
					long resourceId = this.auth.getResourceId((String) rawData.get("resourceName"));
					String methodType = ((String) rawData.get("methodName")).toLowerCase();
					//Here 1 is Super Admin and 2 is Secondary Admin
					if(accessLevel == 1 || accessLevel == 2){
						// Here null means we want every resource in DB as the user accessing it will be super admin
						List<String> functionNames = this.auth.getFunctionNames(resourceId, methodType);
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),functionNames);
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"unknown admin level");
						return invalidRequestReply;
					}
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid keys");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid json format");
				return invalidRequestReply;
			}
		} catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		} catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		} catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getFunctions function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}
	}
	
	private boolean containsValidKeys(JSONObject rawData) {
		if(rawData.containsKey("resourceName") && rawData.containsKey("methodName"))
			return true;
		else 
			return false;
	}

	@GET
	@Path("/getResources")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getResources(@HeaderParam("userDetails") String accessParam){
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
			try {
				authenticate.validate(adminUserName,apikey, "admin", "get", "getResources");
			} catch (Exception e) {
				log.info("Unautherized user "+userName+" tried to access getResources function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
			}
			Map<String,Integer> resources = new HashMap<String,Integer>();
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1){
				// Here null means we want every resource in DB as the user accessing it will be super admin
				List<String> resourceNames = this.auth.getResourceNames("null");
				List<Integer> resourceIds = this.auth.getResourceIds("null");
				for(int i = 0;i < resourceNames.size() && i < resourceIds.size(); i++){
					resources.put(resourceNames.get(i), resourceIds.get(i));
				}
				return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),resources);
			}
			else if(accessLevel == 2){
				// Here admin means we want every resource except admin in DB as the user accessing it will be admin
				List<String> resourceNames = this.auth.getResourceNames("admin");
				List<Integer> resourceIds = this.auth.getResourceIds("admin");
				for(int i = 0;i < resourceNames.size() && i < resourceIds.size(); i++){
					resources.put(resourceNames.get(i), resourceIds.get(i));
				}
				return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(),resources);
			}
			else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"unknown admin level");
				return invalidRequestReply;
			}
		} catch(NumberFormatException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid value for userType");
			return invalidRequestReply;
		} catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		} catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		} catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in getResources function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}	
	}
	
	@POST
	@Path("/addNewResource")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addNewResource(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request) {
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1){
					JSONObject rawData = utilHelper.contextRequestParser(request);
					String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
					try {
						authenticate.validate(adminUserName,apikey, "admin", "post", "addNewResource");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access addNewResource function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
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
							log.info(userName + " has added new resource");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else{
							log.warn(userName + " tried to access addNewResource without providing any resources names");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"provide resource names");
							return invalidRequestReply;
						}
					}else{
						log.warn(userName + " tried to access addNewResource without giving valid key");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid keys");
						return invalidRequestReply;
					}
			} else if(accessLevel == 2){
				log.warn("Unauthorized user tried to access addNewResource function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your adminLevel doesn't provide access to this function");
				return invalidRequestReply;
			} else {
				log.warn("Unauthorized data in addNewResource function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your accessToken is not acceptable");
				return invalidRequestReply;
			}				
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid input format");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in addNewResource function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
			}
		}				
	}

	
	@POST
	@Path("/addNewFunction")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addNewFunction(@HeaderParam("userDetails") String accessParam,@Context HttpServletRequest request) {
		int errors = 0;
		try{
			/*
			 * decoding encoded apikey given by the admin
			 */
			accessParam = helper.getBase64Decoded(accessParam);
			String apikey = "";
			String userName = "";
			if(utilHelper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = utilHelper.jsonParser(accessParam);
					userName = (String) jsonData.get("userName");
					apikey = (String) jsonData.get("accessToken");
				}else{
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid Keys in accessParam");
					return invalidRequestReply;
				}
			}else{
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessParam is Invalid Json");
				return invalidRequestReply;
			}
			int accessLevel = this.auth.validate(apikey);
			//Here 1 is Super Admin and 2 is Secondary Admin
			if(accessLevel == 1){
				JSONObject rawData = utilHelper.contextRequestParser(request);
				String adminUserName = this.auth.getUserNameRelatedToAccessToken(apikey);
				try {
					authenticate.validate(adminUserName,apikey, "admin", "post", "addNewFunction");
				} catch (Exception e) {
					log.info("Unautherized user "+userName+" tried to access addNewFunction function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
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
								if(functionObject.containsKey("postFunctions")){
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
								if(functionObject.containsKey("putFunctions")){
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
							log.info(userName + " has added new functions");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),null);
						}else{
							log.info(userName + " has added only some functions, there were errors in the requested json");
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase()+" but added only some functions, there were errors in the requested json",null);
						}
					}else{
						log.warn(userName + " tried to access addNewFunction without providing any function names specified");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"provide resource names");
						return invalidRequestReply;
					}
				}else{
					log.warn(userName + " tried to access addNewFunction without giving valid key");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid keys");
					return invalidRequestReply;
				}	
			} else if(accessLevel == 2){
				log.warn("Unauthorized user tried to access addNewFunction function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your adminLevel doesn't provide access to this function");
				return invalidRequestReply;
			} else {
				log.warn("Unauthorized user tried to access addNewFunction function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(),"your accessToken is not acceptable");
				return invalidRequestReply;
			}				
		}catch(NullPointerException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid keys or check whether you are sending base64 encoded values");
			return invalidRequestReply;
		}catch(ClassCastException e){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid values");
			return invalidRequestReply;
		}catch(Exception e){
			if(e instanceof ConnectException){
				log.warn("Connection refused server stopped in addNewFunction function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Connection refused server stopped");
				return invalidRequestReply;
			}else{
				log.warn(e.getMessage());
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.EXPECTATION_FAILED.getStatusCode(), Response.Status.EXPECTATION_FAILED.getReasonPhrase(),"unknown exception caused");
				return invalidRequestReply;
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
