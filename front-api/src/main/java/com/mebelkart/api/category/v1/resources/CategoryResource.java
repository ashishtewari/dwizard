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
import javax.ws.rs.PathParam;
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

import com.codahale.metrics.annotation.Timed;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.category.v1.core.CategoryWrapper;
import com.mebelkart.api.category.v1.dao.CategoryDao;
import com.mebelkart.api.category.v1.helper.CategoryHelperMethods;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.helpers.Authentication;

/**
 * @author Nikhil
 *
 */
@Path("/v1.0")
@Produces({MediaType.APPLICATION_JSON})
public class CategoryResource {
	
	JSONObject headerInputJsonData = new JSONObject();
	CategoryDao categoryDao = null;
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();
	CategoryHelperMethods categoryHelperMethods = new CategoryHelperMethods();
	static Logger errorLog = LoggerFactory.getLogger(CategoryResource.class);
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
	@Path("/category/categories")
	@Timed
	public Object getCategories(@HeaderParam("accessParam")String accessParam) throws InterruptedException, ExecutionException{
		
		try {
			headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
			if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
					/*
					 * validating the accesstoken given by user
					 */
				try {
					authenticate.validate(userName,accessToken, "category", "get", "getCategories");
				} catch(Exception e) {
					errorLog.info("Unautherized user "+userName+" tried to access getCategories function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
						FoldingList<CategoryWrapper> categoryIdFoldingList = categoryDao.getCategoryId(1);
						List<CategoryWrapper> categoryIdList = categoryIdFoldingList.getValues();
						List<Object> categoryList = new ArrayList<Object>();
						for(int i=0;i<categoryIdList.size();i++){
							
						BoolQueryBuilder categoryQuery = QueryBuilders.boolQuery()
					            .must(QueryBuilders.termQuery("_id", categoryIdList.get(i).getCategoryId()));
						
						SearchResponse response = client.prepareSearch("mkcategories")
								   .setTypes("category")
								   .setQuery(categoryQuery)									
								   .execute()
								   .actionGet();	
						 SearchHit[] searchHits = response.getHits().getHits();
							 categoryList.add(searchHits[0].getSource());
					}		
						return new Reply(200,"success",categoryList);
				} else {
					errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
					return invalidRequestReply;
				}
		}
		catch (NullPointerException nullPointer) {
			errorLog.info("accessToken or other fields spelled Incorrectly or mention necessary fields");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or other fields spelled Incorrectly or mention necessary fields");
			return invalidRequestReply;
		}
		catch (ParseException parse) {
			errorLog.info("Specify your requirement in requiredFeilds as array of string");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify your requirement in requiredFeilds as array of string");
			return invalidRequestReply;
		}
		catch (ClassCastException classCast) {
			errorLog.info("Please check the values data types");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please check the values data types");
			return invalidRequestReply;
		}
		catch (IndexNotFoundException indexNotFound) {
			errorLog.info("Index for which you are searching is not found");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Index for which you are searching is not found");
			return invalidRequestReply;
		}
		catch (Exception e) {
			e.printStackTrace();
			errorLog.info("Internal server connection error");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server connection error");
			return invalidRequestReply;
		}
		
	}
	
	
	@GET
	@Path("/category/{id}")
	@Timed
	public Object getCategoryDetails(@HeaderParam("accessParam")String accessParam,@PathParam("id")long categoryId){
		
		try {
			headerInputJsonData = (JSONObject) parser.parse(accessParam);
			if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
				
				try{
					
					authenticate.validate(userName,accessToken, "category", "get", "getCategoryDetails");
					
				}  catch(Exception e) {
					errorLog.info("Unautherized user "+userName+" tried to access getCategoryDetails function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				
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
					errorLog.info("categoryId "+ categoryId+" you mentioned was invalid");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "categoryId "+ categoryId+" you mentioned was invalid");
					return invalidRequestReply;
				}
			} else {
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
		
		} 
		catch (NullPointerException nullPointer) {
			errorLog.info("accessToken or other fields spelled Incorrectly or mention necessary fields");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or other fields spelled Incorrectly or mention necessary fields");
			return invalidRequestReply;
		}
		catch (ParseException parse) {
			errorLog.info("Specify your requirement in requiredFeilds as array of string");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify your requirement in requiredFeilds as array of string");
			return invalidRequestReply;
		}
		catch (ClassCastException classCast) {
			errorLog.info("Please check the values data types");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please check the values data types");
			return invalidRequestReply;
		}
		catch (IndexNotFoundException indexNotFound) {
			errorLog.info("Index for which you are searching is not found");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Index for which you are searching is not found");
			return invalidRequestReply;
		}
		catch (Exception e) {
			e.printStackTrace();
			errorLog.info("Internal server connection error");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server connection error");
			return invalidRequestReply;
		}
	}
}
