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
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = null;
		JSONArray requiredFields;
		try {
			jsonObject = (JSONObject) parser.parse(accessParam);
			//try{
			String accessToken = (String) jsonObject.get("apikey");
			long customerId = (long) jsonObject.get("customerid");
			System.out.println("customerId = " + customerId);
			 requiredFields =  (JSONArray)jsonObject.get("required_fields");
			 FoldingList<CustomerDetailsWrapper>customerDetailsResultSet = null;
			 List<CustomerDetailsWrapper> customerDetailsResultSetValues = null;
			 List<String> customerRequiredFields = getRequiredCustomerDetails(requiredFields);
			 List<CustomerDetailsWrapper> customerIndividualDetails = null;

				CustomerApiAuthentication customerAuthentication = new CustomerApiAuthentication(customerAuthDao,customerDetailsDao,accessToken);
				if (customerAuthentication.isAuthKeyValid()) {
					if (customerAuthentication.isCustomerPermitted() ) {
						if(customerAuthentication.isCustomerIdValid(customerId)){
							if(customerRequiredFields.size()==0){
								customerDetailsResultSet = customerDetailsDao.getCustomerDetails(customerId);
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
							return new ChangeToJson(200,"success",customerDetailsResultSetValues);
							}else {
								customerDetailsResultSet = customerDetailsDao.getCustomerDetails(customerId);
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
								//System.out.println("resultSet Values Size = " + customerDetailsResultSetValues.size());
								customerIndividualDetails = getIndividualMappingList(customerDetailsResultSetValues,customerRequiredFields);
								return new ChangeToJson(200,"success",customerIndividualDetails);
							}
						} else{
							HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getCustomerIdNotValidException();
						}
					} else {
						HandleException exception = new HandleException(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
						return exception.getMethodNotAllowedException();
					}
				} else {
					HandleException exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
					return exception.getUnAuthorizedException();
				}
//			} catch (Exception e) {
//				HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
//				return exception.getCustomerIdNullException();
//			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			HandleException exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getContentTypeNullValueException();
		}
		
		
	}

	/**
	 * @param customerDetailsResultSetValues 
	 * @param requiredCustomerDetails 
	 * @return list with details of consumer requested
	 */
	//@SuppressWarnings("null")
	private List<CustomerDetailsWrapper> getIndividualMappingList(List<CustomerDetailsWrapper> customerDetailsResultSetValues, List<String> requiredCustomerDetails) {
		// TODO Auto-generated method stub
		if(!requiredCustomerDetails.contains("firstname")){
			customerDetailsResultSetValues.get(0).setFirstName(null);
		}
		if(!requiredCustomerDetails.contains("lastname")){
			customerDetailsResultSetValues.get(0).setLastName(null);
		}
		if(!requiredCustomerDetails.contains("email")){
			customerDetailsResultSetValues.get(0).setEmail(null);
		}
		if(!requiredCustomerDetails.contains("address1")){
			customerDetailsResultSetValues.get(0).setAddress1(null);
		}
		if(!requiredCustomerDetails.contains("address2")){
			customerDetailsResultSetValues.get(0).setAddress2(null);
		}
		if(!requiredCustomerDetails.contains("mobile")){
			customerDetailsResultSetValues.get(0).setMobile(null);
		}
		if(!requiredCustomerDetails.contains("city")){
			customerDetailsResultSetValues.get(0).setCity(null);
		}
		if(!requiredCustomerDetails.contains("state")){
			customerDetailsResultSetValues.get(0).setState(null);
		}
		if(!requiredCustomerDetails.contains("postcode")){
			customerDetailsResultSetValues.get(0).setPostCode(null);
		}
		
		try{
			for(int i=0;i<customerDetailsResultSetValues.get(0).getOrders().size();i++){
				if(!requiredCustomerDetails.contains("orderId")){
					customerDetailsResultSetValues.get(0).getOrders().get(i).setOrderId(null);
				}
				if(!requiredCustomerDetails.contains("totalPaid")){
					customerDetailsResultSetValues.get(0).getOrders().get(i).setTotalPaid(null);
				}
			}
		}catch(Exception e){
		}
		
		
		return customerDetailsResultSetValues;
	}

	/**
	 * @param requiredFields list given by the consumer
	 * @return field names list which customer asked
	 */
	private List<String> getRequiredCustomerDetails(JSONArray requiredFields) {
		// TODO Auto-generated method stub
		List<String>requiredDetailsList = new ArrayList<String>();
		for(int i=0;i<requiredFields.size();i++){
			requiredDetailsList.add((String) requiredFields.get(i));
		}
		
		return requiredDetailsList;
	}

	

	
	

	
	
}
