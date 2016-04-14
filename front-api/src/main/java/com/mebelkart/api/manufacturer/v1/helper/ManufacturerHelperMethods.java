/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.helper;



import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerHelperMethods {
	
	
	ManufacturerDetailsDAO manufacturerDetailsDao;
	public ManufacturerHelperMethods(ManufacturerDetailsDAO manufacturerDetailsDao) {
		this.manufacturerDetailsDao = manufacturerDetailsDao;
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
	 * @param manufacturerId 
	 * @param client 
	 * @return
	 */
	public boolean isManufacturerIdValid(long manufacturerId, Client client) {
		SearchResponse response = client.prepareSearch("mk").setTypes("manufacturer")
                .setQuery(QueryBuilders.matchQuery("manufacturerId", manufacturerId))
                .setSize(2)
                .execute()
                .actionGet();
		if(response.getHits().getTotalHits()>0){
			return true;
		}
		return false;
	}



//	/**
//	 * @param source
//	 * @return
//	 */
//	public ManufacturerOrders changeOrdersToCamelCase(Map<String, Object> source) {
//		// TODO Auto-generated method stub
//		ManufacturerOrders orders = new ManufacturerOrders();
//		orders.setCustomerId((int) source.get("id_customer"));
//		orders.setManufacturerId((int) source.get("id_manufacturer"));
//		orders.setOrderId((int) source.get("id_order"));
//		orders.setProductId((int) source.get("product_id"));
//		orders.setProductName(source.get("product_name").toString());
//		/*
//		 * first changing the below double variables as string and parsing into double 
//		 * because sometimes input was integer so that we will get classcast exception 
//		 */
//		orders.setProductPrice(Double.parseDouble(source.get("product_price").toString()));
//		orders.setTotalDiscount(Double.parseDouble(source.get("total_discounts").toString()));
//		orders.setTotalPaid(Double.parseDouble(source.get("total_paid").toString()));
//		orders.setTotalProducts(Double.parseDouble(source.get("total_products").toString()));
//		orders.setTotalShipping(Double.parseDouble(source.get("total_shipping").toString()));
//		orders.setDateAdd(source.get("date_add").toString());
//		return orders;
//	}



	/**
	 * @param requiredFields
	 * @return
	 */
	public String[] getRequiredDetails(JSONArray requiredFields) {
		String consumerRequestedFieldsArray[] = new String[requiredFields.size()];
		int count=0;
		if(requiredFields.contains("name")){
			consumerRequestedFieldsArray[count] = "name";
			count++;
		}
		if(requiredFields.contains("email")){
			consumerRequestedFieldsArray[count] = "email";
			count++;
		}
		if(requiredFields.contains("dateAdded")){
			consumerRequestedFieldsArray[count] = "dateAdded";
			count++;
		}
		if(requiredFields.contains("orders")){
			consumerRequestedFieldsArray[count] = "manufacturerOrders";
			count++;
		}
		if(requiredFields.contains("products")){
			consumerRequestedFieldsArray[count] = "manufacturerProducts";
			count++;
		}
		if(requiredFields.contains("address")){
			consumerRequestedFieldsArray[count] = "manufacturerAddresses";
			count++;
		}
		if(requiredFields.contains("company")){
			consumerRequestedFieldsArray[count] = "manufacturerCompanyInfo";
			count++;
		}
		return consumerRequestedFieldsArray;
	}

}
