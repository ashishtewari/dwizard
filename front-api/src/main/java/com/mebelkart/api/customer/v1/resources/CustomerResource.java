/**
 * 
 */
package com.mebelkart.api.customer.v1.resources;


import java.util.ArrayList;
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

import com.github.rkmk.container.FoldingList;
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
	
	CustomerAuthenticationDAO customerAuthDao;
	CustomerDetailsDAO customerDetailsDao;
	
	public CustomerResource(CustomerAuthenticationDAO customerAuth,CustomerDetailsDAO customerDetails){
		this.customerAuthDao = customerAuth;
		this.customerDetailsDao = customerDetails;
	}
	
	@GET
	@Path("")
	public ChangeToJson noQueryParams() {
		HandleException exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
		return exception.getNoQueryParamsException();
	}
	
	/**
	 * Returns the customer details as per requested parameters
	 * @throws ParseException 
	 */

	@GET
	@Path("/getDetails")
	public Object getCustomerDetails(@HeaderParam("accessParam")String accessParam) throws ParseException{
		System.out.println("entered in getDetails");
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONArray requiredFields;
		//try {
			jsonObject = (JSONObject) parser.parse(accessParam);
			String accessToken = (String) jsonObject.get("apikey");
			long customerId = (long) jsonObject.get("customerid");
			 requiredFields =  (JSONArray)jsonObject.get("required_fields");
			 FoldingList<CustomerDetailsWrapper>customerDetailsResultSet = null;
			 List<CustomerDetailsWrapper> customerDetailsResultSetValues = null;
			 List<String> requiredCustomerDetails = getRequiredCustomerDetails(requiredFields);
			 

				CustomerApiAuthentication customerAuthentication = new CustomerApiAuthentication(customerAuthDao,accessToken);
				if (customerAuthentication.isAuthKeyValid()) {
					if (customerAuthentication.isCustomerPermitted(1) ) {
						if(requiredCustomerDetails.size()==0){
						FoldingList<CustomerDetailsWrapper>customerDetails = customerDetailsDao.getCustomerDetails(customerId);
					List<CustomerDetailsWrapper> customerAllDetailsList =  customerDetails.getValues();
						return new ChangeToJson(200,"success",customerAllDetailsList);
						}else {
							if(requiredCustomerDetails.contains("firstname")){
								customerDetailsResultSet = customerDetailsDao.getCustomerPersonalDetails(customerId);
								customerDetailsResultSetValues = customerDetailsResultSet.getValues();
						} if(requiredCustomerDetails.contains("lastname")){
							customerDetailsResultSet = customerDetailsDao.getCustomerPersonalDetails(customerId);
							customerDetailsResultSetValues = customerDetailsResultSet.getValues();
						}
						
							return new ChangeToJson(200,"success",customerDetailsResultSetValues);
						}
					} else {
						HandleException exception = new HandleException(Response.Status.NOT_ACCEPTABLE.getStatusCode(),Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
						return exception.getAccessLimitExceededException();
					}
				} else {
					HandleException exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getUnAuthorizedException();
				}
//			} catch (Exception e) {
//				System.out.println("entered in first catch");
//				HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
//				return exception.getNullValueException();
//			}
//		
//		} catch (Exception e1) {
//			// TODO Auto-generated catch block
//			HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
//			return exception.getNullValueException();
//		}
		
		
	}

	/**
	 * @param requiredFields
	 * @return
	 */
	private List<String> getRequiredCustomerDetails(JSONArray requiredFields) {
		// TODO Auto-generated method stub
		List<String>requiredDetailsArray = new ArrayList<String>();
		for(int i=0;i<requiredFields.size();i++){
			requiredDetailsArray.add((String) requiredFields.get(i));
		}
		
		return requiredDetailsArray;
	}
	
	

	
	
}
