/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;


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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.customer.v1.core.CustomerApiAuthentication;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.ChangeToJson;
import com.mebelkart.api.customer.v1.helper.CustomerRequestedDetails;
import com.mebelkart.api.util.HandleException;

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
	public CustomerResource(CustomerAuthenticationDAO customerAuth,CustomerDetailsDAO customerDetails){
		this.customerAuthDao = customerAuth;
		this.customerDetailsDao = customerDetails;
	}
	
	/**
	 * Returns the customer details as per requested parameters
	 * @throws ParseException 
	 */

	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("accessParam")String accessParam) throws ParseException{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONArray requiredFields;
		try {
			jsonObject = (JSONObject) parser.parse(accessParam);
			String accessToken = (String) jsonObject.get("apikey");
			long customerId = (long) jsonObject.get("customerid");
			 requiredFields =  (JSONArray)jsonObject.get("required_fields");
			 CustomerRequestedDetails requestedDetails = new CustomerRequestedDetails();
			 FoldingList<CustomerDetailsWrapper>customerDetailsResultSet = null;
			 List<CustomerDetailsWrapper> customerDetailsResultSetValues = null;
			 String customerRequiredDetails = "";

				CustomerApiAuthentication customerAuthentication = new CustomerApiAuthentication(customerAuthDao,customerDetailsDao,accessToken);
				if (customerAuthentication.isAuthKeyValid()) {
					if (customerAuthentication.isCustomerPermitted() ) {
						if(customerAuthentication.isCustomerIdValid(customerId)){
							if(requiredFields.size()==0){
								customerDetailsResultSet = customerDetailsDao.getCustomerDetails(customerId);
								customerDetailsResultSetValues = customerDetailsResultSet.getValues();
								return new ChangeToJson(200,"success",customerDetailsResultSetValues);
							}
							else {
								customerRequiredDetails = requestedDetails.getRequiredDetailsString(requiredFields);
								customerDetailsResultSet = customerDetailsDao.getRequiredCustomerDetails(customerId,customerRequiredDetails);
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
								return new ChangeToJson(200,"success",customerDetailsResultSetValues);
							}
						} else{
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("CustomerId you mentioned was invalid",null);
						}
					} else {
						exception = new HandleException(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
						return exception.getException("You are not authorized to perform this action",null);
					}
				} else {
					exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getException("Invalid API key specified or key is inActive",null);
				}

			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Content-Type or apikey or customerid or required_fields spelled Incorrectly",null);
		}
		
		
	}
}
