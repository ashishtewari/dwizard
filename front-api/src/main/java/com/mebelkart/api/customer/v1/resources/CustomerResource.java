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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;



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
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.CustomerHelperMethods;
import com.mebelkart.api.util.exceptions.HandleException;
import com.mebelkart.api.util.factories.JedisFactory;
import com.mebelkart.api.util.Reply;

/**
 * @author Nikky-Akky
 *
 */

@Path("/v1.0/customer")
@JsonInclude(Include.NON_NULL)
public class CustomerResource {
	
	CustomerDetailsDAO customerDetailsDao;
	HandleException exception = null;
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
	@Path("/getCustomerDetails")
	@Produces({ MediaType.APPLICATION_JSON })
	public Reply getCustomerDetails(@HeaderParam("accessParam")String accessParam) throws ParseException, ConnectException{
		
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			if(helperMethods.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = (String) headerInputJsonData.get("apiKey");
				String userName = (String) headerInputJsonData.get("userName");
				long customerId = (long) headerInputJsonData.get("customerId");
				requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
				int isAccessTokenValid = jedisCustomerAuthentication.validate(userName,accessToken, "customer", "get","getCustomerDetails");
					/*
					 * validating the accesstoken given by user
					 */
				if (isAccessTokenValid == 1) {
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
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("CustomerId "+ customerId+" you mentioned was invalid",null);
					}
				} else {
					exception = new HandleException();
					return exception.accessTokenException(isAccessTokenValid);
				}
			} else {
				errorLog.warn("The parameter which you specified is not in json format");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("The parameters which you specified is not in json format",null);
			}	
		}
			catch (NullPointerException nullPointer) {
				errorLog.warn("apiKey or customerId spelled Incorrectly or mention necessary fields of address");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("apiKey or customerId spelled Incorrectly or mention necessary fields of address",null);
			}
			catch (ClassCastException classCast) {
				errorLog.warn("Give apiKey as String,customerid as integer,requiredFields as array of strings");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Give apiKey as String,customerid as integer,requiredFields as array of strings",null);
			}
			catch (ParseException parse) {
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify correct data type for the values as mentioned in CustomerReadme doc",null);
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
					return exception.getException("Specify correct keys for the values as mentioned in CustomerReadme doc",null);
				} else {
					exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
					return exception.getException("Internal server error",null);
				}
			}	
	}
	
	
	@SuppressWarnings("unused")
	@PUT
	@Path("/addNewAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Reply addNewAddress(@Context HttpServletRequest request) throws ParseException{
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request);
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			int isAddressAdded=0,isAccessTokenValid = jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put","addNewAddress");
			long customerId = (long) bodyInputJsonData.get("customerId");
				if (isAccessTokenValid == 1) { // validating the accesstoken given by user
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
							errorLog.warn(isValidInputValues); // adding error response from validateInputValues method
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException(helperMethods.validateInputValues(bodyInputJsonData),null);
						}
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("CustomerId "+ customerId+" you mentioned was invalid",null);
					}
				} else {
					exception = new HandleException();
					return exception.accessTokenException(isAccessTokenValid);
				}
			} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId spelled Incorrectly or mention necessary fields of address");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("apiKey or customerId spelled Incorrectly or mention necessary fields of address",null);
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify correct data type for the values as mentioned in CustomerReadme doc",null);
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify correct keys for the values as mentioned in CustomerReadme doc",null);
			} else {
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Internal server error",null);
			}
		}
}
	
	@PUT
	@Path("/updateAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Reply updateAddress(@Context HttpServletRequest request) throws ParseException{
		try {
			String getUpdateDetails = "";
			String splitUpdateDetails[]=null;
			int isAddressUpdated = 0;
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request); 
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			long customerId = (long) bodyInputJsonData.get("customerId");
			long addressId = (long)bodyInputJsonData.get("addressId");
			int isAccessTokenValid = jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put","updateAddress");
				/*
				 * validating the accesstoken given by user
				 */
				if (isAccessTokenValid == 1) { 
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
									exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
									errorLog.warn("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
									return exception.getException("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"",null);
								}
									
						} else {
							errorLog.warn(getUpdateDetails);
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException(""+getUpdateDetails+"",null); // if there is any error in validations then this will return that error.
						}
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("CustomerId "+ customerId+" you mentioned was invalid",null);
					}
				} else {
					exception = new HandleException();
					return exception.accessTokenException(isAccessTokenValid);
					}
				} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId or addressId spelled Incorrectly");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("apiKey or customerid or addressId spelled Incorrectly",null);
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify correct data type for the values as mentioned in CustomerReadme doc",null);
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify correct keys for the values as mentioned in CustomerReadme doc",null);
			} else {
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Internal server error",null);
			}
		}
}
	
	@DELETE
	@Path("/deleteAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	public Reply deleteAddress(@Context HttpServletRequest request) throws ParseException{
		try {
			helperMethods = new CustomerHelperMethods(customerDetailsDao);
			bodyInputJsonData = helperMethods.contextRequestParser(request); 
			String accessToken = (String) bodyInputJsonData.get("apiKey");
			String userName = (String) bodyInputJsonData.get("userName");
			int isAddressDeleted=0,isAccessTokenValid = jedisCustomerAuthentication.validate(userName,accessToken, "customer", "put", "deleteAddress");
			long customerId = (long) bodyInputJsonData.get("customerId");
			long addressId = (long)bodyInputJsonData.get("addressId");
				if (isAccessTokenValid == 1) { // validating the accesstoken given by user
					if(helperMethods.isCustomerIdValid(customerId)){ // checking whether the customerId is valid or not
						/*
						 * Below method if given addressId and customerId matches then  query runs and return 1 else return 0.
						 */
						isAddressDeleted = customerDetailsDao.deleteAddress(addressId,customerId);
							
						if (isAddressDeleted == 1) { 
							errorLog.info("Address of addressId:"+addressId+" and customerId:"+ customerId+" deleted successfully by user:"+userName+" on "+ helperMethods.getDateTime());
							return new Reply(201,"success","Address of addressId "+addressId+" deleted succesfully" );
							
						} else { // return customerId and addressId not matched exception
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							errorLog.warn("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"");
							return exception.getException("AddressId "+addressId+" was not matched to the addressId's of customerId "+ customerId+"",null);
						}
						
					} else {
						errorLog.warn("CustomerId "+ customerId+" you mentioned was invalid");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("CustomerId "+ customerId+" you mentioned was invalid",null);
					}
				} else {
					exception = new HandleException();
					return exception.accessTokenException(isAccessTokenValid);
				}
			} 	
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or customerId or addressId spelled Incorrectly");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("apiKey or customerid or addressId spelled Incorrectly",null);
		}	
		catch (ClassCastException classCast) {
			errorLog.warn("Specify correct data type for the values as mentioned in CustomerReadme doc");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify correct data type for the values as mentioned in CustomerReadme doc",null);
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify correct keys for the values as mentioned in instructions");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify correct keys for the values as mentioned in CustomerReadme doc",null);
			} else {
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Internal server error",null);
			}
		}
	}
}
