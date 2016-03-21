/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;


import java.net.ConnectException;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;



import javax.ws.rs.Produces;
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
import com.mebelkart.api.customer.v1.core.CustomerApiAuthentication;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.CustomerRequestedDetails;
import com.mebelkart.api.util.HandleException;
import com.mebelkart.api.util.Reply;

/**
 * @author Nikky-Akky
 *
 */

@Path("/v1.0/customer")
@JsonInclude(Include.NON_NULL)
@Produces({ MediaType.APPLICATION_JSON })
public class CustomerResource {
	
	CustomerAuthenticationDAO customerAuthDao;
	CustomerDetailsDAO customerDetailsDao;
	HandleException exception = null;
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	public CustomerResource(CustomerAuthenticationDAO customerAuth,CustomerDetailsDAO customerDetails){
		this.customerAuthDao = customerAuth;
		this.customerDetailsDao = customerDetails;
	}
	
	/**
	 * @return Returns the customer details as per requested parameters
	 * @throws ParseException 
	 */

	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("accessParam")String accessParam) throws ParseException, ConnectException{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONArray requiredFields;
		try {
			jsonObject = (JSONObject) parser.parse(accessParam);
			String accessToken = (String) jsonObject.get("apikey");
			long customerId = (long) jsonObject.get("customerid");
			 requiredFields =  (JSONArray)jsonObject.get("requiredFields");
			 CustomerRequestedDetails requestedDetails = new CustomerRequestedDetails();
			 FoldingList<CustomerDetailsWrapper>customerDetailsResultSet = null;
			 List<CustomerDetailsWrapper> customerDetailsResultSetValues = null;
			 List<String> customerRequiredDetails = null;

				CustomerApiAuthentication customerAuthentication = new CustomerApiAuthentication(customerAuthDao,customerDetailsDao,accessToken);
				if (customerAuthentication.isAuthKeyValid()) {
					if (customerAuthentication.isCustomerPermitted() ) {
						if(customerAuthentication.isCustomerIdValid(customerId)){
							if(requiredFields.size()==0){
								customerDetailsResultSet = customerDetailsDao.getCustomerDetails(customerId);
								customerDetailsResultSetValues = customerDetailsResultSet.getValues();
								return new Reply(200,"success",customerDetailsResultSetValues);
							}
							else {
								customerRequiredDetails = requestedDetails.getRequiredDetailsString(requiredFields);
								customerDetailsResultSet = customerDetailsDao.getRequiredCustomerDetails(customerId,customerRequiredDetails.get(0),customerRequiredDetails.get(1),customerRequiredDetails.get(2));
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
								return new Reply(200,"success",customerDetailsResultSetValues);
							}
						} else{
							errorLog.warn("CustomerId you mentioned was invalid");
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("CustomerId you mentioned was invalid",null);
						}
					} else {
						errorLog.warn("You are not authorized to perform this operation");
						exception = new HandleException(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
						return exception.getException("You are not authorized to perform this action",null);
					}
				} else {
					errorLog.warn("Invalid API key specified or key is inActive");
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("Invalid API key specified or key is inActive",null);
				}

			
		} 	catch (NullPointerException nullPointer) {
			errorLog.warn("Content-Type or apikey or customerid or required_fields spelled Incorrectly");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Content-Type or apikey or customerid or required_fields spelled Incorrectly",null);
		}
			catch (ClassCastException classCast) {
				errorLog.warn("Give apikey as String,customerid as integer,required_fields as array of strings");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Give apikey as String,customerid as integer,required_fields as array of strings",null);
		}
			catch (ParseException parse) {
				errorLog.warn("Specify your requirement in required_feilds as array of string");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify your requirement in required_feilds as array of string",null);
		}
			catch (Exception e) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Content-Type or apikey or customerid or required_fields spelled Incorrectly",null);
		}
		
		
	}
}
