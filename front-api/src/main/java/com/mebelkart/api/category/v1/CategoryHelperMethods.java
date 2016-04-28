/**
 * 
 */
package com.mebelkart.api.category.v1;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author Nikhil
 *
 */
public class CategoryHelperMethods {

	/**
	 * @param categoryId
	 * @param client
	 * @return
	 */
	public boolean isCategoryIdValid(long categoryId, Client client) {
		SearchResponse response = client.prepareSearch("categories-1.12.6").setTypes("category")
                .setQuery(QueryBuilders.termQuery("_id", categoryId))
                .setSize(2)
                .execute()
                .actionGet();
		if(response.getHits().getTotalHits()>0){
			return true;
		}
		return false;
	}
	
}
