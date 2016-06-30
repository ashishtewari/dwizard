/**
 * 
 */
package com.mebelkart.api.category.v1.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

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

	/**
	 * @param i
	 * @param client 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Object> getCategoriesBasedOnDepth(int id, Client client) {
		// TODO Auto-generated method stub
		List<Object> categoryList = new ArrayList<Object>();
		Map<String,Object>categoryMap = new HashMap<String,Object>();
		Map<String,Object>categoryMap1 = null;
		BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
	            .must(QueryBuilders.matchQuery("data.id_parent", id));
		
		SearchResponse response = client.prepareSearch("mkcategories")
				   .setTypes("category")
				   .setSearchType(SearchType.QUERY_AND_FETCH)
				   .setFetchSource(new String[]{"data.id_category","data.name","data.link_rewrite","data.imageLinkMedium"}, null)
				   .setQuery(categoryQuery)									
				   .execute()
				   .actionGet();	
		 SearchHit[] searchHits = response.getHits().getHits();
//		 System.out.println("source = " + searchHits[0].getSource());
//		 System.out.println("data = " + searchHits[0].getSource().get("data"));
//		 System.out.println("id = " + searchHits[0].getSource().get("data.id_category"));
		 for(int i=0;i<searchHits.length;i++){
			 categoryMap1 = ((Map<String, Object>) searchHits[i].getSource().get("data"));
			 categoryMap.put("id",categoryMap1.get("id_category"));
			 categoryMap.put("name",categoryMap1.get("name"));
			 categoryMap.put("link_rewrite",categoryMap1.get("link_rewrite"));
			 categoryMap.put("image",categoryMap1.get("imageLinkMedium"));
//			 System.out.println("id = " + categoryMap1.get("id_category"));
//			 System.out.println("name = " + categoryMap1.get("name.1"));
//			 System.out.println("name1 = " + categoryMap1.get("name"));
//			 System.out.println("name = " + searchHits[i].getSource().get("name.1"));
//			 System.out.println("name = " + searchHits[i].getSource().get("id_category"));
			 categoryList.add(categoryMap1);
		 }
		return categoryList;
	}
	
}
