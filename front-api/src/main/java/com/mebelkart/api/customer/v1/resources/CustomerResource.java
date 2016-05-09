/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;


import java.net.ConnectException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;



import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.util.crypting.MD5Encoding;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.CustomerHelperMethods;
import com.mebelkart.api.util.factories.JedisFactory;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;

/**
 * @author Nikky-Akky
 *
 */

@Path("/v1.0/customer")
@JsonInclude(Include.NON_NULL)
public class CustomerResource {
	
	CustomerDetailsDAO customerDetailsDao;
	InvalidInputReplyClass invalidRequestReply = null;
	CustomerHelperMethods helperMethods = null;
	JSONParser parser = new JSONParser();
	JSONObject headerInputJsonData = null,bodyInputJsonData = null;
	JSONArray requiredFields;
	JedisFactory jedisCustomerAuthentication = new JedisFactory();
	MD5Encoding encode = new MD5Encoding();
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	
	public CustomerResource(CustomerDetailsDAO customerDetails){
		this.customerDetailsDao = customerDetails;
	}
	
	/**
	 * @return Returns the customer details as per requested parameters
	 * @throws ParseException 
	 */	
	
	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Object getCustomerDetails(@HeaderParam("accessParam")String accessParam,@PathParam("id")long customerId) throws ParseException, ConnectException{
		
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			if(helperMethods.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = (String) headerInputJsonData.get("apiKey");
				String userName = (String) headerInputJsonData.get("userName");
				requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
				try {
						/*
						 * validating the accesstoken given by user
						 */
					jedisCustomerAuthentication.validate(userName,accessToken, "customer", "get","getCustomerDetails");
				} catch(Exception e) {
					errorLog.info("Unautherized user "+userName+" tried to access getCustomerDetails function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
					    /*
					     * checking whether the customerId is valid or not
					     */
					if(helperMethods.isCustomerIdValid(customerId)){ 
						
						FoldingList<CustomerDetailsWrapper>customerFoldingListResultSet = null; // folding list is to fold database resultset for dynamic mapping.
						List<CustomerDetailsWrapper> customerFoldingListResultSetValues = null; // url for resource jdbi folder http://manikandan-k.github.io/jdbi_folder/
						List<String> customerRequiredDetails = null;
						if(requiredFields.size()==0){ // if the user wants all the details
							/*
							 * Adding customerDetails resultSet to foldingList
							 */
							customerFoldingListResultSet = customerDetailsDao.getCustomerDetails(customerId);
							/*
							 * Retrieving values from foldingList and adding them to a list
							 */
							customerFoldingListResultSetValues = customerFoldingListResultSet.getValues();
							return new Reply(200,"success",customerFoldingListResultSetValues);
						}
						else {
							/*
							 * getting required fields string to append directly to query in dao class
							 */
							customerRequiredDetails = helperMethods.getRequiredDetailsString(requiredFields);
						
							customerFoldingListResultSet = customerDetailsDao.getRequiredCustomerDetails(customerId,customerRequiredDetails.get(0),customerRequiredDetails.get(1),customerRequiredDetails.get(2));
							
							customerFoldingListResultSetValues =  customerFoldingListResultSet.getValues();
							return new Reply(200,"success",customerFoldingListResultSetValues);
						}
					} else{
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "CustomerId "+ customerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
			} else {
				errorLog.warn("The parameter which you specified is not in json format");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "The parameter which you specified is not in json format");
				return invalidRequestReply;
			}	
		}
			catch (NullPointerException nullPointer) {
				errorLog.warn("apiKey or customerId spelled Incorrectly or mention necessary fields of address");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "apiKey or customerId spelled Incorrectly or mention necessary fields of address");
				return invalidRequestReply;
			}
			catch (ClassCastException classCast) {
				errorLog.warn("Give apiKey as String,customerid as integer,requiredFields as array of strings");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Give apiKey as String,customerid as integer,requiredFields as array of strings");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				} else {
					e.printStackTrace();
					errorLog.warn("Internal server error");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
					return invalidRequestReply;
				}
			}	
	}
	
	
	@SuppressWarnings("unused")
	@POST
	@Path("/address")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object addNewAddress(@Context HttpServletRequest request) throws ParseException{
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request);
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			int isAddressAdded=0;
			try{
				jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put","addNewAddress");
				
			} catch(Exception e) {
				errorLog.warn("Unautherized user "+userName+" tried to access addNewAddress function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), "Unautherized user "+userName+" tried to access addNewAddress function");
				return invalidRequestReply;
			}
			
				long customerId = (long) bodyInputJsonData.get("customerId");
					if(helperMethods.isCustomerIdValid(customerId)){ // checking whether the customerId is valid or not
						String isValidInputValues = helperMethods.validateInputValues(bodyInputJsonData);
						     /*
						      * validating input data given by the user
						      */
						if(isValidInputValues.equals("success")){ 
								/*
								 * adding all the new address details to the query
								 */
							isAddressAdded = customerDetailsDao.addNewAddress((long)bodyInputJsonData.get("countryId"),(long)bodyInputJsonData.get("stateId"),customerId,bodyInputJsonData.get("alias").toString().replaceAll("[^a-zA-Z0-9 ]", ""),bodyInputJsonData.get("firstName").toString().replaceAll("[^a-zA-Z0-9 ]", ""), bodyInputJsonData.get("lastName").toString().replaceAll("[^a-zA-Z0-9 ]", ""), bodyInputJsonData.get("address1").toString().replaceAll("[^a-zA-Z0-9-/, ]", ""), bodyInputJsonData.get("address2").toString().replaceAll("[^a-zA-Z0-9-/, ]", ""), (String)bodyInputJsonData.get("postCode"), bodyInputJsonData.get("city").toString().replaceAll("[^a-zA-Z0-9 ]", ""), (String)bodyInputJsonData.get("mobile"));
							    /*
							     * adding response info to the log file
							     */
							errorLog.info("New address added for customerId:"+ customerId+" by consumer:"+userName+" on "+ helperMethods.getDateTime());
							return new Reply(201,"success","New address added succesfully to customerId " + customerId);
							
						} else {
							errorLog.warn(isValidInputValues);
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), isValidInputValues);
							return invalidRequestReply;
						}
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "CustomerId "+ customerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
			} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId spelled Incorrectly or mention necessary fields of address");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "apiKey or customerId spelled Incorrectly or mention necessary fields of address");
			return invalidRequestReply;
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please check the values data types");
			return invalidRequestReply;
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
				return invalidRequestReply;
			} else {
				e.printStackTrace();
				errorLog.warn("Internal server error");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
				return invalidRequestReply;
			}
		}
}
	
	@PUT
	@Path("/address/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object updateAddress(@Context HttpServletRequest request,@PathParam("id")long customerId) throws ParseException{
		try {
			String getUpdateDetails = "";
			String splitUpdateDetails[]=null;
			int isAddressUpdated = 0;
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request); 
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			long addressId = (long)bodyInputJsonData.get("addressId");
			try {
					/*
					 * validating the accesstoken given by user
					 */
				jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put","updateAddress");
				
			} catch(Exception e) {
				errorLog.info("Unautherized user "+userName+" tried to access updateAddress function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
			}
						/*
						 * checking whether the customerId is valid or not
						 */
					if (helperMethods.isCustomerIdValid(customerId)) { 
						/*
						 * getting update query if the given values pass the validations else error message.
						 */
						getUpdateDetails = helperMethods.getUpdateDetailsString(bodyInputJsonData);
							
							// splitting the returned string.
						splitUpdateDetails = getUpdateDetails.split(" "); 
							/*
							 * checking whether the string has error in it.
							 */
						if(!splitUpdateDetails[0].trim().equals("Error")){
							isAddressUpdated = customerDetailsDao.updateAddress(addressId,customerId,getUpdateDetails);
									/*
									 * if the given addressId and customerId matches then query runs and return 1 else return 0.
									 */
								if (isAddressUpdated == 1) { 
									errorLog.info("Address of addressId:"+addressId+" and customerId:"+ customerId+" updated successfully by user:"+userName+" on "+ helperMethods.getDateTime());
									return new Reply(201,"success","Address of addressId "+addressId+" updated succesfully" );
								} else {
									errorLog.warn("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
									invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
									return invalidRequestReply;
								}
									
						} else {
							errorLog.warn(getUpdateDetails);
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), getUpdateDetails);
							return invalidRequestReply;
						}
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "CustomerId "+ customerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
				} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId or addressId spelled Incorrectly");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "apiKey or customerId or addressId spelled Incorrectly");
			return invalidRequestReply;
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please check the values data types");
			return invalidRequestReply;
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
				return invalidRequestReply;
			} else {
				e.printStackTrace();
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
				return invalidRequestReply;
			}
		}
}
	
	@DELETE
	@Path("/deleteAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Object deleteAddress(@Context HttpServletRequest request) throws ParseException{
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request); 
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			int isAddressDeleted=0;
			try{
				jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put", "deleteAddress");
			} catch(Exception e) {
				errorLog.info("Unautherized user "+userName+" tried to access deleteAddress function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
			}
				long customerId = (long) bodyInputJsonData.get("customerId");
				long addressId = (long)bodyInputJsonData.get("addressId");
					if(helperMethods.isCustomerIdValid(customerId)){ // checking whether the customerId is valid or not
						/*
						 * Below method if given addressId and customerId matches then  query runs and return 1 else return 0.
						 */
						isAddressDeleted = customerDetailsDao.deleteAddress(addressId,customerId);
							
						if (isAddressDeleted == 1) { 
							errorLog.info("Address of addressId:"+addressId+" and customerId:"+ customerId+" deleted successfully by user:"+userName+" on "+ helperMethods.getDateTime());
							return new Reply(201,"success","Address of addressId "+addressId+" deleted succesfully" );
							
						} else { // return customerId and addressId not matched exception
							errorLog.warn("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
							return invalidRequestReply;
						}
						
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "CustomerId "+ customerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
			} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId or addressId spelled Incorrectly");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "apiKey or customerId or addressId spelled Incorrectly");
			return invalidRequestReply;
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Specify correct data type for the values as mentioned in CustomerReadme doc");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in CustomerReadme doc");
			return invalidRequestReply;
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
				return invalidRequestReply;
			} else {
				e.printStackTrace();
				errorLog.warn("Internal server error");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
				return invalidRequestReply;
			}
		}
	}
}
