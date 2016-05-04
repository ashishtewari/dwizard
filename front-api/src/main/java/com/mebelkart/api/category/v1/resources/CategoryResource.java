/**
 * 
 */
package com.mebelkart.api.category.v1.resources;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
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

import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.category.v1.core.CategoryWrapper;
import com.mebelkart.api.category.v1.dao.CategoryDao;
import com.mebelkart.api.category.v1.helper.CategoryHelperMethods;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
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
	
	JSONObject headerInputJsonData = new JSONObject();
	CategoryDao categoryDao = null;
	JedisFactory jedisCustomerAuthentication = new JedisFactory();
	CategoryHelperMethods categoryHelperMethods = new CategoryHelperMethods();
	static Logger errorLog = LoggerFactory.getLogger(CategoryResource.class);
	HandleException exception = null;
	InvalidInputReplyClass invalidRequestReply = null;
	JSONParser parser = new JSONParser();
	Client client = ElasticFactory.getProductsElasticClient();
	
	/**
	 * @param categoryDao
	 */
	public CategoryResource(CategoryDao categoryDao) {
		this.categoryDao = categoryDao;
	}	
	
		/*
		 * Below method is for getting top categories 
		 */
	@GET
	@Path("/getCategories")
	public Object getCategories(@HeaderParam("accessParam")String accessParam) throws InterruptedException, ExecutionException{
		
		try {
			headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
			String accessToken = headerInputJsonData.get("apiKey").toString();
			String userName = headerInputJsonData.get("userName").toString();
				/*
				 * validating the accesstoken given by user
				 */
			try {
				jedisCustomerAuthentication.validate(userName,accessToken, "category", "get", "getCategories");
			} catch(Exception e) {
				errorLog.info("Unautherized user "+userName+" tried to access getCategories function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
				return invalidRequestReply;
			}
				/*
				 * checking whether given categoryId is valid or not
				 */
					FoldingList<CategoryWrapper> categoryIdFoldingList = categoryDao.getCategoryId(1);
					List<CategoryWrapper> categoryIdList = categoryIdFoldingList.getValues();
					List<Object> categoryList = new ArrayList<Object>();
					for(int i=0;i<categoryIdList.size();i++){
						
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
				            .must(QueryBuilders.termQuery("_id", categoryIdList.get(i).getCategoryId()));
					
//					GetResponse response1 = client.prepareGet("categories-1.12.6", "category", categoryIdList.get(i).getCategoryId()+"")
//					        .execute()
//					        .actionGet();
					
					SearchResponse response = client.prepareSearch("mkcategories")
							   .setTypes("category")
							   .setQuery(categoryQuery)									
							   .execute()
							   .actionGet();	
					 SearchHit[] searchHits = response.getHits().getHits();
						 categoryList.add(searchHits[0].getSource());
				}
				
					return new Reply(200,"success",categoryList);
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
	
	
	@GET
	@Path("/getCategoryDetails")
	public Object getCategoryDetails(@HeaderParam("accessParam")String accessParam){
		
		try {
			headerInputJsonData = (JSONObject) parser.parse(accessParam);
			String accessToken = headerInputJsonData.get("apiKey").toString();
			String userName = headerInputJsonData.get("userName").toString();
			long categoryId = (long) headerInputJsonData.get("categoryId");
			try{
				jedisCustomerAuthentication.validate(userName,accessToken, "category", "get", "getCategoryDetails");
				if(categoryHelperMethods.isCategoryIdValid(categoryId,client)){
					List<Object> categoryList = new ArrayList<Object>();
					BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
				            .must(QueryBuilders.termQuery("_id",categoryId));
					
					SearchResponse response = client.prepareSearch("mkcategories")
							   .setTypes("category")
							   .setQuery(categoryQuery)									
							   .execute()
							   .actionGet();	
					
					 SearchHit[] searchHits = response.getHits().getHits();
					 for(int i=0;i<searchHits.length;i++){
						 categoryList.add(searchHits[i].getSource());
					 }
					 
			return new Reply(200,"success",categoryList);
		
			} else {
				errorLog.warn("categoryId you mentioned was invalid");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("categoryId "+ categoryId+" you mentioned was invalid",null);
			}
		}  catch(Exception e) {
			errorLog.info("Unautherized user "+userName+" tried to access getCategoryDetails function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
			return invalidRequestReply;
		}
		
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