/**
 * 
 */
package com.mebelkart.api.customer.v1.helper;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerHelperMethods {
	
	
	CustomerDetailsDAO customerDetailsDao;
	public CustomerHelperMethods(CustomerDetailsDAO customerDetailsDao) {
		this.customerDetailsDao = customerDetailsDao;
	}
	
	/**
	 * This method parse Json string to JsonObject
	 * @param data of JsonString
	 * @return JSONObject
	 */
	public JSONObject jsonParser(String data) {
		JSONParser parser = new JSONParser();
		JSONObject dataObj = null;
		try {
			dataObj = (JSONObject) parser.parse(data);
		} catch (ParseException e) {
			return null;
		}
		return dataObj;
	}
	
	
	/**
	 * @return true if json is valid or false if not valid json
	 */
	public boolean isValidJson(String accessParam) {
		if(jsonParser(accessParam) == null)
			return false;
		else
			return true;
	}
	
	
	/**
	 * It converts servlet request of type Json to JsonString
	 * @param request consists of body data of type raw json data
	 * @return JSONObject
	 */
	public JSONObject contextRequestParser(HttpServletRequest request) {
		InputStream inputStream = null;
		String jsonString = null;
		try {
			inputStream = request.getInputStream();
			jsonString = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			
		}
		return jsonParser(jsonString);
	}
	
	
	/**
	 * @param customerId
	 * @return true if customerId is valid else false.
	 */
	public boolean isCustomerIdValid(long customerId) {
		List<CustomerDetailsWrapper> customerIdList = this.customerDetailsDao.getCustomerId(customerId);
		if(customerIdList.size()>0){
			return true;
		}
		return false;
	}
	
	/**
	 * @param requiredFields list given by the consumer
	 * @return String to be appended in query. So that query works dynamically as per the customer needs 
	 */
	public List<String> getRequiredDetailsString(JSONArray requiredDetailsArray){
		List<String> queryStrings = new ArrayList<String>();
		String selectQueryString = "",joinQueryString="",orderByQueryString="";
		boolean isAddressDetailsAsked = false;
		/*
		 * this will return the query strings separately like select string,join string and orderby string,this will return as per the 
		 * consumer request 
		 */
		if(requiredDetailsArray.contains("firstname")){
			selectQueryString = selectQueryString+"ps_customer.firstname,";
		}
		if(requiredDetailsArray.contains("lastname")){
			selectQueryString = selectQueryString+"ps_customer.lastname,";
		}
		if(requiredDetailsArray.contains("email")){
			selectQueryString = selectQueryString+"ps_customer.email,";
		}
		
		if(! requiredDetailsArray.contains("address")){	
			if(requiredDetailsArray.contains("address1")){
				selectQueryString = selectQueryString+"ps_address.address1 AS address$address1,";
				isAddressDetailsAsked=true;
			}
			if(requiredDetailsArray.contains("address2")){
				selectQueryString = selectQueryString+"ps_address.address2 AS address$address2,";
				isAddressDetailsAsked=true;
			}
			if(requiredDetailsArray.contains("mobile")){
				selectQueryString = selectQueryString+"ps_address.phone_mobile AS address$phone_mobile,";
				isAddressDetailsAsked=true;
			}
			if(requiredDetailsArray.contains("city")){
				selectQueryString = selectQueryString+"ps_address.city AS address$city,";
				isAddressDetailsAsked=true;
			}
			if(requiredDetailsArray.contains("state")){
				selectQueryString = selectQueryString+"ps_state.name AS address$name,";
				isAddressDetailsAsked=true;
			}
			if(requiredDetailsArray.contains("postcode")){
				selectQueryString = selectQueryString+"ps_address.postcode AS address$postcode,";
				isAddressDetailsAsked=true;
			}
		} else {
			selectQueryString = selectQueryString
					+ "ps_address.id_address AS address$id_address,ps_address.address1 AS address$address1,ps_address.address2 AS address$address2, "
					+ "ps_address.phone_mobile AS address$phone_mobile,ps_address.city AS address$city,ps_state.name AS address$name, "
					+ "ps_address.postcode AS address$postcode,";
			isAddressDetailsAsked=true;
		}
		
		/*
		 * checking whether user asked the details of address if yes appending the address Id to the query string
		 * and also appending join query of address table
		 */
		if(isAddressDetailsAsked){ 
			selectQueryString = "ps_address.id_address AS address$id_address,"+selectQueryString;
			joinQueryString = joinQueryString
					+ "LEFT JOIN ps_address ON ps_customer.id_customer=ps_address.id_customer "
					+ "LEFT JOIN ps_state ON ps_address.id_state=ps_state.id_state ";
		}
		
		/*
		 * checking whether user asked the details of wishlist if yes appending the wishlist details to the query string
		 * and also appending join query of wishlist table
		 */
		if(requiredDetailsArray.contains("wishlist")){
			selectQueryString = selectQueryString
					+ "ps_wishlist.id_wishlist AS wishlist$id_wishlist,ps_wishlist.name AS wishlist$name,"
					+ "ps_wishlist.date_add AS wishlist$date_add,ps_wishlist.date_upd AS wishlist$date_upd,";
			joinQueryString = joinQueryString+"LEFT JOIN ps_wishlist ON ps_wishlist.id_customer=ps_customer.id_customer ";
		}
		
		/*
		 * checking whether user asked the details of orders if yes appending the orders details to the query string
		 * and also appending join query of orders table
		 */
		if(requiredDetailsArray.contains("orders")){
			selectQueryString = selectQueryString
					+ "ps_orders.id_order AS orders$id_order,ps_orders.total_products AS orders$total_products,"
					+ "ps_orders.total_paid AS orders$total_paid,ps_orders.total_discounts AS orders$total_discounts,"
					+ "ps_message.id_message AS messages$id_message,ps_message.message AS messages$message,"
					+ "ps_message.date_add AS messages$date_add,";
			joinQueryString = joinQueryString+"LEFT JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer "
					+ "LEFT JOIN ps_message ON ps_message.id_customer=ps_customer.id_customer and ps_message.id_order=ps_orders.id_order";
			orderByQueryString = orderByQueryString+"ORDER BY ps_message.id_message DESC";
		}
		System.out.println("selectQueryString = " + selectQueryString);
		selectQueryString = selectQueryString.substring(0, selectQueryString.length()-1); // removing the comma at the end of the string
		queryStrings.add(selectQueryString);
		queryStrings.add(joinQueryString.trim());
		queryStrings.add(orderByQueryString);
		return queryStrings;
	
		}


	/**
	 * @param bodyInputJsonData
	 * @return query string for updateAddress if the validations are fine else it will return the error string
	 */
	public String getUpdateDetailsString(JSONObject bodyInputJsonData) {
		String updateDetails = "",errorResponse = "Error ",jsonKeyValue = "";
		int updateFieldsCount = 0;
		System.out.println("json data size = " + bodyInputJsonData.size());
			/*
			 * jsonKeyValue is for storing values of keys given like firstname,lastname. once after storing we can use it anywhere
			 */
		if(bodyInputJsonData.containsKey("firstName")){
			jsonKeyValue = bodyInputJsonData.get("firstName").toString().replaceAll("[^a-zA-Z0-9 ]", "");
			if(jsonKeyValue.length()!=0  && !jsonKeyValue.matches(".*\\d.*")){
				updateDetails = updateDetails+"ps_address.firstname='"+jsonKeyValue+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"firstName is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether lastName field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("lastName")){
			jsonKeyValue = bodyInputJsonData.get("lastName").toString().replaceAll("[^a-zA-Z0-9 ]", "");
			if(jsonKeyValue.length()!=0  && !jsonKeyValue.matches(".*\\d.*")){
				updateDetails = updateDetails+"ps_address.lastname='"+jsonKeyValue+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"lastName is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether address1 field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("address1")){
			jsonKeyValue = bodyInputJsonData.get("address1").toString().replaceAll("[^a-zA-Z0-9 ]", "");
			if(jsonKeyValue.length()!=0){
				updateDetails = updateDetails+"ps_address.address1='"+bodyInputJsonData.get("address1").toString().replaceAll("[^a-zA-Z0-9-/, ]", "")+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"address1 is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether address2 field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("address2")){
			jsonKeyValue = bodyInputJsonData.get("address2").toString().replaceAll("[^a-zA-Z0-9 ]", "");
			if(jsonKeyValue.toString().length()!=0){
				updateDetails = updateDetails+"ps_address.address2='"+bodyInputJsonData.get("address2").toString().replaceAll("[^a-zA-Z0-9-/, ]", "")+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"address2 is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether mobile field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("mobile")){
			jsonKeyValue = bodyInputJsonData.get("mobile").toString();
			if(jsonKeyValue.length()==10 && StringUtils.isNumeric(jsonKeyValue)){
				updateDetails = updateDetails+"ps_address.phone_mobile='"+jsonKeyValue+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"mobile is needed and it should be 10 digits";
				return errorResponse;
			}
		}
			/*
			 * checking whether city field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("city")){
			jsonKeyValue = bodyInputJsonData.get("city").toString().replaceAll("[^a-zA-Z0-9 ]", "");
			if(jsonKeyValue.length()!=0  && !jsonKeyValue.matches(".*\\d.*")){// validating the city string,if it contains any number return error 
				updateDetails = updateDetails+"ps_address.city='"+jsonKeyValue+"',";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"city is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether stateId field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("stateId")){
			if(bodyInputJsonData.get("stateId").toString().length()!=0){
				updateDetails = updateDetails+"ps_address.id_state="+(long)bodyInputJsonData.get("stateId")+",";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"stateId is needed and it should not be null";
				return errorResponse;
			}
		}
			/*
			 * checking whether postCode field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("postCode")){
			if(bodyInputJsonData.get("postCode").toString().length()==6  && StringUtils.isNumeric(bodyInputJsonData.get("postCode").toString())){
				updateDetails = updateDetails+"ps_address.id_postcode="+(long)bodyInputJsonData.get("postCode")+",";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"postCode is needed and it should be 6 digits";
				return errorResponse;
			}
		}
			/*
			 * checking whether countryId field is updating or not, if required then checking validations
			 */
		if(bodyInputJsonData.containsKey("countryId")){
			if(bodyInputJsonData.get("countryId").toString().length()!=0){
				updateDetails = updateDetails+"ps_address.id_country="+(long)bodyInputJsonData.get("countryId")+",";
				updateFieldsCount++;
			}else{
				errorResponse = errorResponse+"countryId is needed and it should not be null";
				return errorResponse;
			}
		}
		/*
		 * checking whether given fields are spelled correctly to update that address details.
		 * Total size-3 is removing apikey,customerId,addressId from the count.because we 
		 * should not consider those three fields.
		 */
		if(updateFieldsCount != (bodyInputJsonData.size()-3)){
			errorResponse = errorResponse+"please check the field names which you had given.Some of them are misspelled";
			return errorResponse;
		}
		
		return updateDetails;
	}


	/**
	 * @return current time stamp
	 */
	public String getDateTime() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm:ss a");
		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}


	/**
	 * @param bodyInputJsonData 
	 * @return "success" if the input values are validated successfully else return error message.
	 */
	public String validateInputValues(JSONObject bodyInputJsonData) {
		String response = "";
		if(!(bodyInputJsonData.containsKey("firstName")) || !(bodyInputJsonData.get("firstName").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0) || bodyInputJsonData.get("firstName").toString().matches(".*\\d.*")){
			response = response+"Error firstName is needed and it should not be null";
			return response;
		}else
			if(!(bodyInputJsonData.containsKey("lastName")) || !(bodyInputJsonData.get("lastName").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0) || bodyInputJsonData.get("lastName").toString().matches(".*\\d.*")){
				response = response+"Error lastName is needed and it should not be null";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("address1")) || !(bodyInputJsonData.get("address1").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0)){
				response = response+"Error address1 is needed and it should not be null";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("address2")) || !(bodyInputJsonData.get("address2").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0)){
				response = response+"Error address2 key is needed";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("mobile")) || !(bodyInputJsonData.get("mobile").toString().length()==10)|| !StringUtils.isNumeric(bodyInputJsonData.get("mobile").toString())){
				response = response+"Error mobile is needed and it should be 10 digits";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("city")) || !(bodyInputJsonData.get("city").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0)|| bodyInputJsonData.get("city").toString().matches(".*\\d.*")){
				response = response+"Error city is needed and it should not be null";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("stateId")) || !(bodyInputJsonData.get("stateId").toString().length()!=0)){
				response = response+"Error stateId is needed and it should not be null";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("countryId")) || !(bodyInputJsonData.get("countryId").toString().length()!=0)){
				response = response+"Error countryId is needed and it should not be null";
				return response;
		}else
			if(!(bodyInputJsonData.containsKey("postCode")) || !(bodyInputJsonData.get("postCode").toString().length()==6) || !StringUtils.isNumeric(bodyInputJsonData.get("postCode").toString())){
				response = response+"Error postCode is needed and it should be 6 digits";
				return response;
		}else 
			if(!(bodyInputJsonData.containsKey("alias")) || !(bodyInputJsonData.get("alias").toString().replaceAll("[^a-zA-Z0-9 ]", "").length()!=0)){
				response = response+"Error alias is needed and it should not be null";
				return response;
		}else{
			
			return "success";
		}
	}
}
