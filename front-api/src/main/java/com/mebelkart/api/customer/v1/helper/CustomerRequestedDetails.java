/**
 * 
 */
package com.mebelkart.api.customer.v1.helper;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerRequestedDetails {
	
	/**
	 * @param requiredFields list given by the consumer
	 * @return String to be appended in query. So that query works dynamically as per the customer needs 
	 */
	public List<String> getRequiredDetailsString(JSONArray requiredDetailsArray){
		List<String> queryStrings = new ArrayList<String>();
		String selectQueryString = "",queryJoinString="";
		boolean isAddressDetailsAsked = false;
		
		if(requiredDetailsArray.contains("firstname")){
			selectQueryString = selectQueryString+"ps_customer.firstname,";
		}
		if(requiredDetailsArray.contains("lastname")){
			selectQueryString = selectQueryString+"ps_customer.lastname,";
		}
		if(requiredDetailsArray.contains("email")){
			selectQueryString = selectQueryString+"ps_customer.email,";
		}
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
		
		/*
		 * checking whether user asked the details of address if yes appending the address Id to the query string
		 * and also appending join query of address table
		 */
		if(isAddressDetailsAsked){ 
			selectQueryString = "ps_address.id_address AS address$id_address,"+selectQueryString;
			queryJoinString = queryJoinString+"LEFT JOIN ps_address ON ps_customer.id_customer=ps_address.id_customer LEFT JOIN ps_state ON ps_address.id_state=ps_state.id_state ";
		}
		
		/*
		 * checking whether user asked the details of wishlist if yes appending the wishlist details to the query string
		 * and also appending join query of wishlist table
		 */
		if(requiredDetailsArray.contains("wishlist")){
			selectQueryString = selectQueryString+"ps_wishlist.id_wishlist AS wishlist$id_wishlist,ps_wishlist.name AS wishlist$name,ps_wishlist.date_add AS wishlist$date_add,ps_wishlist.date_upd AS wishlist$date_upd,";
			queryJoinString = queryJoinString+"LEFT JOIN ps_wishlist ON ps_wishlist.id_customer=ps_customer.id_customer ";
		}
		
		/*
		 * checking whether user asked the details of orders if yes appending the orders details to the query string
		 * and also appending join query of orders table
		 */
		if(requiredDetailsArray.contains("orders")){
			selectQueryString = selectQueryString+"ps_orders.id_order AS orders$id_order,ps_orders.total_products AS orders$total_products,ps_orders.total_paid AS orders$total_paid,ps_orders.total_discounts AS orders$total_discounts,";
			queryJoinString = queryJoinString+"LEFT JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer ";
		}
		
		selectQueryString = selectQueryString.substring(0, selectQueryString.length()-1); // removing the comma at the end of the string
		queryStrings.add(selectQueryString);
		queryStrings.add(queryJoinString.trim());
		return queryStrings;
	
		} 


}
