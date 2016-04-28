/**
 * 
 */
package com.mebelkart.api.category.v1.resources;


import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.category.v1.CategoryHelperMethods;
import com.mebelkart.api.util.exceptions.HandleException;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.factories.JedisFactory;

/**
 * @author Nikhil
 *
 */
@Path("/v1.0/category")
@Produces({MediaType.APPLICATION_JSON})
public class CategoryResource {
	
	JSONObject headerInputJsonData = null;
	JedisFactory jedisCustomerAuthentication = new JedisFactory();
	CategoryHelperMethods categoryHelperMethods = new CategoryHelperMethods();
	static Logger errorLog = LoggerFactory.getLogger(CategoryResource.class);
	HandleException exception = null;
	JSONParser parser = new JSONParser();
	Client client = ElasticFactory.getProductsElasticClient();
	
	@Path("/getCategoryDetails")
	public Object getCategoryDetails(@HeaderParam("accessParam")String accessParam){
		try {
			headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
			String accessToken = headerInputJsonData.get("apiKey").toString();
			String userName = headerInputJsonData.get("userName").toString();
			long categoryId = (long) headerInputJsonData.get("categoryId");
				/*
				 * validating the accesstoken given by user
				 */
			//int isUserAuthorized = jedisCustomerAuthentication.validate(userName,accessToken, "category", "get", "getCategoryDetails");
			//if (isUserAuthorized == 1) {
				/*
				 * checking whether given categoryId is valid or not
				 */
				if(categoryHelperMethods.isCategoryIdValid(categoryId,client)){
					
					List<Object> categoryList = new ArrayList<Object>();
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
				            .must(QueryBuilders.termQuery("_id", categoryId));
					
					SearchResponse response = client.prepareSearch("categories-1.12.6")
							   .setTypes("category")
							   .setQuery(categoryQuery)									
//							   .setFrom(page*paginationLimit)
//							   .setSize(paginationLimit)
							   .execute()
							   .get();	
					 SearchHit[] searchHits = response.getHits().getHits();
					 
					 for(int i=0;i<searchHits.length;i++){
						 categoryList.add(searchHits[i].getSource());
					 }
				
					return accessParam;
					
				} else {
					errorLog.warn("categoryId you mentioned was invalid");
					exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
					return exception.getException("categoryId "+ categoryId+" you mentioned was invalid",null);
				}
//			} else {
//				exception = new HandleException();
//				return exception.accessTokenException(isUserAuthorized);
//			}
		}
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or manufacturerId spelled Incorrectly or mention necessary fields of address");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("apiKey or manufacturerId spelled Incorrectly or mention necessary fields",null);
		}
		catch (ParseException parse) {
			errorLog.warn("Specify your requirement in requiredFeilds as array of string");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify your requirement in requiredFeilds as array of string",null);
		}
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify correct data type for the values as mentioned in instructions",null);
		}
		catch (IndexNotFoundException indexNotFound) {
			errorLog.warn("Index for which you are searching is not found");
			exception = new HandleException(Response.Status.NOT_FOUND.getStatusCode(),Response.Status.NOT_FOUND.getReasonPhrase());
			return exception.getException("Index for which you are searching is not found",null);
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify date format correctly and it should not be null");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify date format correctly and it should not be null",null);
			} else {
				e.printStackTrace();
				errorLog.warn("Internal server connection error");
				exception = new HandleException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());
				return exception.getException("Internal server connection error",null);
			}
		}
		
	}

}
