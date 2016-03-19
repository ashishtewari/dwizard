/**
 * 
 */
package com.mebelkart.api.customer.v1.helper;

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
	public String getRequiredDetailsString(JSONArray requiredDetailsArray){
		
		String selectQueryString = "";
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
			selectQueryString = selectQueryString+"ps_state.state AS address$state,";
			isAddressDetailsAsked=true;
		}
		if(requiredDetailsArray.contains("postcode")){
			selectQueryString = selectQueryString+"ps_address.postcode AS address$postcode,";
			isAddressDetailsAsked=true;
		}
		if(isAddressDetailsAsked){ // checking whether user asked the details of address if yes appending the address Id to the string
			selectQueryString = "ps_address.id_address AS address$id_address,"+selectQueryString;
		}
		if(requiredDetailsArray.contains("wishlist")){
			selectQueryString = selectQueryString+"ps_wishlist.id_wishlist AS wishlist$id_wishlist,ps_wishlist.name AS wishlist$name,ps_wishlist.date_add AS wishlist$date_add,ps_wishlist.date_upd AS wishlist$date_upd,";
		}
		if(requiredDetailsArray.contains("orders")){
			selectQueryString = selectQueryString+"ps_orders.id_order AS orders$id_order,ps_orders.total_products AS orders$total_products,ps_orders.total_paid AS orders$total_paid,ps_orders.total_discounts AS orders$total_discounts,";
		}
		selectQueryString = selectQueryString.substring(0, selectQueryString.length()-1); // removing the comma at the end of the string
		return selectQueryString;
	
			
		} 


}
