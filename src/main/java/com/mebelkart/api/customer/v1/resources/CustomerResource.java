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
import org.json.simple.parser.ParseException;

import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;
import com.mebelkart.api.customer.v1.helper.ChangeToJson;
import com.mebelkart.api.customer.v1.helper.HandleException;

/**
 * @author Nikky-Akky
 *
 */

@Path("/customer")
@Produces({ MediaType.APPLICATION_JSON })
public class CustomerResource {
	
	CustomerAuthenticationDAO customerAuth;
	CustomerDetailsDAO customerDetails;
	
	public CustomerResource(CustomerAuthenticationDAO customerAuth,CustomerDetailsDAO customerDetails){
		this.customerAuth = customerAuth;
		this.customerDetails = customerDetails;
	}
	
	@GET
	@Path("/test")
	public Object test(){
		ChangeToJson result = new ChangeToJson(200, "Success", "hello World");
		return result;
		
	}
	/**
	 * Returns the customer details as per requested parameters
	 */
	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("apikey")String key,@QueryParam("customerid")int customerId){
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(key);
			key = (String) obj.get("key");
					List<CustomerDetailsWrapper>productDetails = customerDetails.getCustomerDetails(customerId);
						return new ChangeToJson(200,"success",productDetails);
		
		} catch (ParseException e1) {
			HandleException exception = new HandleException(
					Response.Status.BAD_REQUEST.getStatusCode(),
					Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getNullValueException();
		}
		
		
	}
	
	

	
	
}
