/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;



import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mebelkart.api.customer.v1.core.CustomerApiAuthentication;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.helper.ChangeToJson;
import com.mebelkart.api.customer.v1.helper.HandleException;

/**
 * @author Nikky-Akky
 *
 */

@Path("/customer")
@Produces({ MediaType.APPLICATION_JSON })
public class CustomerResource {
	
	CustomerAuthenticationDAO customer;
	
	public CustomerResource(CustomerAuthenticationDAO customer){
		this.customer = customer;
	}
	
	@GET
	@Path("/test")
	public Object test(){
		System.out.println("entered in test");
		ChangeToJson result = new ChangeToJson(200, "Success", "hello World");
		return result;
		
	}
	/**
	 * Returns the customer details as per requested parameters
	 */
	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("apikey")String key,@QueryParam("customerid")String customerId){
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(key);
			key = (String) obj.get("key");
			System.out.println("key = "+key +","+ "customerId = " + customerId);
			try {
				CustomerApiAuthentication authentication = new CustomerApiAuthentication(customer,key);
				if (authentication.isAuthKeyValid()) {
					if (authentication.isCustomerPermitted(1)) {
						ChangeToJson json = new ChangeToJson(200, "success", "hello World");
						return json;
					} else {
						HandleException exception = new HandleException(
								Response.Status.NOT_ACCEPTABLE.getStatusCode(),
								Response.Status.NOT_ACCEPTABLE
										.getReasonPhrase());
						return exception.getAccessLimitExceededException();
					}
				} else {
					HandleException exception = new HandleException(
							Response.Status.UNAUTHORIZED.getStatusCode(),
							Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getUnAuthorizedException();
				}
			} catch (Exception e) {
				HandleException exception = new HandleException(
						Response.Status.BAD_REQUEST.getStatusCode(),
						Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getNullValueException();
			}
		
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();g
			System.out.println("entered in first try block");
			HandleException exception = new HandleException(
					Response.Status.BAD_REQUEST.getStatusCode(),
					Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getNullValueException();
		}
		
		
	}
	
	

	
	
}
