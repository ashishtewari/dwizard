/**
 * 
 */
package com.mebelkart.api.category.v1.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;

import com.mebelkart.api.category.v1.dao.CategoryDao;

/**
 * @author Nikhil
 *
 */
public class CategoryHelperMethods {
	
	CategoryDao categoryDao = null;
	
	public CategoryHelperMethods(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}

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
	public List<Object> getCategoriesBasedOnDepth(String categoryId, Client client) {
		// TODO Auto-generated method stub
		List<Object> categoryList = new ArrayList<Object>();
//		Map<String,Object>categoryMap = new HashMap<String,Object>();
//		Map<String,Object>categoryMap1 = null;
		Map<String,Object> topCategoriesMap = new HashMap<String,Object>();
		GetResponse response = client.prepareGet("mkcategories", "category", categoryId)
				.setFields("data.id_category","data.name.1","data.link_rewrite.1","data.imageLinkMedium")
				.execute()
				.actionGet();
		topCategoriesMap.put("category_id",categoryId);
		topCategoriesMap.put("category_name", response.getField("data.name.1").getValue() );
		topCategoriesMap.put("imag_url", response.getField("data.imageLinkMedium").getValue() );
		topCategoriesMap.put("title", response.getField("data.name.1").getValue() );
		topCategoriesMap.put("children",getCategoriesChildren(categoryId, client) );
		categoryList.add(topCategoriesMap);
		return categoryList;
	}

	/**
	 * @param categoryId
	 * @param client
	 * @return
	 */
	private Object getCategoriesChildren(String categoryId, Client client) {
		// TODO Auto-generated method stub
//		int depth = 1;
		List<Integer> categoryIdsList = categoryDao.getCategoryId(Integer.parseInt(categoryId));
		List<Object> categoryList = new ArrayList<Object>();
		System.out.println("category list size = " + categoryIdsList.size());
		Map<String,Object> topCategoriesMap = new HashMap<String,Object>();
//		while(depth != 3 ){
			for(int i=0; i<categoryIdsList.size(); i++){
				GetResponse response = client.prepareGet("mkcategories", "category", categoryId)
						.setFields("data.id_category","data.name.1","data.link_rewrite.1","data.imageLinkMedium")
						.execute()
						.actionGet();
				topCategoriesMap.put("category_id",categoryId);
				topCategoriesMap.put("category_name", response.getField("data.name.1").getValue() );
				topCategoriesMap.put("imag_url", response.getField("data.imageLinkMedium").getValue() );
				topCategoriesMap.put("title", response.getField("data.name.1").getValue() );
				topCategoriesMap.put("children",getCategoriesChildren(categoryId, client) );
				categoryList.add(topCategoriesMap);
			}
//		}
		return categoryList;
	}

	/**
	 * @param categoryId
	 * @param offerText 
	 * @param brandId 
	 * @param client 
	 * @return 
	 */
	public Map<String, Object> getTopCategoryDetailsFromElastic(String categoryId, int brandId, String offerText, Client client) {
		// TODO Auto-generated method stub
		Map<String,Object> topCategoriesMap = new HashMap<String,Object>();
		GetResponse response = client.prepareGet("mkcategories", "category", categoryId)
				.setFields("data.name.1","data.id_image")
				.execute()
				.actionGet();
		
		topCategoriesMap.put("brand_id", brandId+"");
		topCategoriesMap.put("category_name", response.getField("data.name.1").getValue() );
		topCategoriesMap.put("imag_url", response.getField("data.id_image").getValue() );
		topCategoriesMap.put("title", response.getField("data.name.1").getValue() );
		topCategoriesMap.put("offer_text",offerText);
		topCategoriesMap.put("category_id",categoryId);
		return topCategoriesMap;
	}
	
}
