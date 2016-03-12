/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;


import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;



import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mebelkart.api.customer.v1.core.CustomerApiAuthentication;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.ChangeToJson;
import com.mebelkart.api.customer.v1.helper.HandleException;

/**
 * @author Nikky-Akky
 *
 */

@Path("/v1/customer")
@Produces({ MediaType.APPLICATION_JSON })
public class CustomerResource {
	
	CustomerAuthenticationDAO customerAuth;
	CustomerDetailsDAO customerDetails;
	
	public CustomerResource(CustomerAuthenticationDAO customerAuth,CustomerDetailsDAO customerDetails){
		this.customerAuth = customerAuth;
		this.customerDetails = customerDetails;
	}
	
	@GET
	@Path("")
	public ChangeToJson noQueryParams() {
		HandleException exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
		return exception.getNoQueryParamsException();
	}
	
	/**
	 * Returns the customer details as per requested parameters
	 */
	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("apikey")String apiKey,@QueryParam("customerid")int customerId){
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(apiKey);
			apiKey = (String) obj.get("apikey");
			try {
				CustomerApiAuthentication authentication = new CustomerApiAuthentication(customerAuth,apiKey);
				if (authentication.isAuthKeyValid()) {
					if (authentication.isCustomerPermitted(1)) {
					List<CustomerDetailsWrapper>productDetails = customerDetails.getCustomerDetails(customerId);
						return new ChangeToJson(200,"success",productDetails);
					} else {
						HandleException exception = new HandleException(Response.Status.NOT_ACCEPTABLE.getStatusCode(),Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
						return exception.getAccessLimitExceededException();
					}
				} else {
					HandleException exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getUnAuthorizedException();
				}
			} catch (Exception e) {
				HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getNullValueException();
			}
		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getNullValueException();
		}
		
		
	}
	
	

	
	
}
