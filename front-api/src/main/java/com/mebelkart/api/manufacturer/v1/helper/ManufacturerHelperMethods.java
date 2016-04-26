/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.helper;



import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
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
		SearchResponse response = client.prepareSearch("manufacturer").setTypes("info")
                .setQuery(QueryBuilders.termQuery("_id", manufacturerId))
                .setSize(2)
                .execute()
                .actionGet();
		if(response.getHits().getTotalHits()>0){
			return true;
		}
		return false;
	}

	/**
	 * @param object
	 * @param client 
	 */
	public Map<String, Object> getOrderDetailsFromElastic(int orderId, Client client) {
		System.out.println("orderId in helper method = " + orderId);
			SearchResponse response = client.prepareSearch("mk").setTypes("order")
										.setQuery(QueryBuilders.termQuery("_id", orderId))
										.execute()
										.actionGet();
			SearchHit[] searchHits = response.getHits().getHits();
			if(searchHits.length>0){
				return searchHits[0].getSource();
			}else{
				return null;
			}
	}

}
