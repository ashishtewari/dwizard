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
			 requiredFields =  (JSONArray)jsonObject.get("required_fields");
			 FoldingList<CustomerDetailsWrapper>customerDetailsResultSet = null;
			 List<CustomerDetailsWrapper> customerDetailsResultSetValues = null;
			 List<String> customerRequiredFields = getRequiredCustomerDetails(requiredFields);
			 String customerRequiredDetails = "";

				CustomerApiAuthentication customerAuthentication = new CustomerApiAuthentication(customerAuthDao,customerDetailsDao,accessToken);
				if (customerAuthentication.isAuthKeyValid()) {
					if (customerAuthentication.isCustomerPermitted() ) {
						if(customerAuthentication.isCustomerIdValid(customerId)){
							if(customerRequiredFields.size()==0){
								customerDetailsResultSet = customerDetailsDao.getCustomerDetails(customerId);
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
								customerDetailsResultSetValues = removeUnnecessaryCharacters(customerDetailsResultSetValues, customerRequiredFields);
							return new ChangeToJson(200,"success",customerDetailsResultSetValues);
							}else {
								customerRequiredDetails = getRequiredDetailsString(customerRequiredFields);
								customerDetailsResultSet = customerDetailsDao.getRequiredCustomerDetails(customerId,customerRequiredDetails);
								customerDetailsResultSetValues =  customerDetailsResultSet.getValues();
								customerDetailsResultSetValues = removeUnnecessaryCharacters(customerDetailsResultSetValues,customerRequiredFields);
								return new ChangeToJson(200,"success",customerDetailsResultSetValues);
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
	 * @param customerRequiredFields 
	 * @return list by removing unnecessary characters like '\n\r',
	 */
	private List<CustomerDetailsWrapper> removeUnnecessaryCharacters(List<CustomerDetailsWrapper> customerDetailsResultSetValues, List<String> customerRequiredFields) {
		// TODO Auto-generated method stub
		//try{
		for(int i=0;i<customerDetailsResultSetValues.get(0).getAddress().size();i++){
			if(customerRequiredFields.contains("address1")){
			customerDetailsResultSetValues.get(0).getAddress().get(i).setAddress1(customerDetailsResultSetValues.get(0).getAddress().get(i).getAddress1().replaceAll("\\r\\n", ""));
			}
			if(customerRequiredFields.contains("address2")){
				customerDetailsResultSetValues.get(0).getAddress().get(i).setAddress2(customerDetailsResultSetValues.get(0).getAddress().get(i).getAddress2().replaceAll("\\r\\n", ""));
			}
			if(!(customerRequiredFields.contains("address1") || customerRequiredFields.contains("address2"))){
				break;
			}
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
	
	/**
	 * @param requiredFields list given by the consumer
	 * @return String to be appended in query. So that query works dynamically as per the customer needs 
	 */
	private String getRequiredDetailsString(List<String> requiredDetailsList){
		
		String selectQueryString = "";
		boolean isOrderDetailsAsked = false,isAddressDetailsAsked = false;
		
		if(requiredDetailsList.contains("firstname")){
			selectQueryString = selectQueryString+"ps_customer.firstname,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("lastname")){
			selectQueryString = selectQueryString+"ps_customer.lastname,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("email")){
			selectQueryString = selectQueryString+"ps_customer.email,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("address1")){
			selectQueryString = selectQueryString+"ps_address.address1 AS address$address1,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("address2")){
			selectQueryString = selectQueryString+"ps_address.address2 AS address$address2,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("mobile")){
			selectQueryString = selectQueryString+"ps_address.phone_mobile AS address$phone_mobile,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("city")){
			selectQueryString = selectQueryString+"ps_address.city AS address$city,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("state")){
			selectQueryString = selectQueryString+"ps_state.state AS address$state,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsList.contains("postcode")){
			selectQueryString = selectQueryString+"ps_address.postcode AS address$postcode,";
			isAddressDetailsAsked=true;
		}
		if(isAddressDetailsAsked){ // checking whether user asked the details of address if yes appending the address Id to the string
			selectQueryString = "ps_address.id_address AS address$id_address,"+selectQueryString;
		}
		if(requiredDetailsList.contains("wishlist")){
			selectQueryString = selectQueryString+"ps_wishlist.id_wishlist AS wishlist$id_wishlist,ps_wishlist.name AS wishlist$name,ps_wishlist.date_add AS wishlist$date_add,ps_wishlist.date_upd AS wishlist$date_upd,";
		}
		if(requiredDetailsList.contains("orderId")){
			selectQueryString = selectQueryString+"ps_orders.id_order AS orders$id_order,";
			isOrderDetailsAsked = true;
		}
		if(requiredDetailsList.contains("totalPaid")){
			selectQueryString = selectQueryString+"ps_orders.total_paid AS orders$total_paid,";
			isOrderDetailsAsked = true;
		}
		if(isOrderDetailsAsked){// checking whether user asked the details of orders if yes appending the order Id to the string
			selectQueryString = "ps_orders.id_order AS orders$id_order,"+selectQueryString;
		}
		selectQueryString = selectQueryString.substring(0, selectQueryString.length()-1); // removing the comma at the end of the string
		
		return selectQueryString;
		
	}

	

	
	

	
	
}
