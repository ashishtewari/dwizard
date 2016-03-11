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

import com.mebelkart.api.customer.v1.core.CustomerApiAuthentication;
import com.mebelkart.api.customer.v1.core.CustomerWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerDAO;
import com.mebelkart.api.customer.v1.helper.ChangeToJson;
import com.mebelkart.api.customer.v1.helper.HandleException;

/**
 * @author Nikky-Akky
 *
 */

@Path("/customer")
@Produces({ MediaType.APPLICATION_JSON })
public class CustomerResource {
	
	//CustomerDAO customer;
	
	public CustomerResource(){
		//this.customer = customer;
	}
	
	@GET
	@Path("/test")
	public Object test(){
		System.out.println("entered in test");
		ChangeToJson result = new ChangeToJson(200, "Success", "hello World");
		return result;
		
	}
	
//	@Path("/details")
//	public Object getCustomerDetails(@HeaderParam("apikey")String key,@QueryParam("")String paramaeters){
//		CustomerApiAuthentication authentication = new CustomerApiAuthentication(customer,key);
//		try {
//			if (authentication.isAuthKeyValid()) {
//				if (authentication.isCustomerPermitted(0)) {
////					List<CustomerWrapper> product_details = this.customer
////							.findAll();
////					customer.updateCount(key);
////					return new ChangeToJson(200, "Test Success",
////							product_details);
//					return null;
//				} else {
//					HandleException exception = new HandleException(
//							Response.Status.NOT_ACCEPTABLE.getStatusCode(),
//							Response.Status.NOT_ACCEPTABLE
//									.getReasonPhrase());
//					return exception.getAccessLimitExceededException();
//				}
//			} else {
//				HandleException exception = new HandleException(
//						Response.Status.UNAUTHORIZED.getStatusCode(),
//						Response.Status.UNAUTHORIZED.getReasonPhrase());
//				return exception.getUnAuthorizedException();
//			}
//		} catch (Exception e) {
//			HandleException exception = new HandleException(
//					Response.Status.BAD_REQUEST.getStatusCode(),
//					Response.Status.BAD_REQUEST.getReasonPhrase());
//			return exception.getNullValueException();
//		}
//	
//		
//	}
//	
	

	
	
}
