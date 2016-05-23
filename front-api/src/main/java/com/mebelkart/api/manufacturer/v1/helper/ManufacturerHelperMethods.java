/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.helper;



import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerHelperMethods {

	/**
	 * @param manufacturerId 
	 * @param client 
	 * @return
	 */
	public boolean isManufacturerIdValid(long manufacturerId, Client client) {
		SearchResponse response = client.prepareSearch("mkmanufacturer").setTypes("manufacturerInfo")
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
			SearchResponse response = client.prepareSearch("orders").setTypes("order")
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
