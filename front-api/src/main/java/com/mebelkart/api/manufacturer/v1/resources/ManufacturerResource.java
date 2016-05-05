/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.resources;

import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.helper.ManufacturerHelperMethods;
import com.mebelkart.api.util.helpers.Helper;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.ManufacturerPaginationReply;
import com.mebelkart.api.util.exceptions.HandleException;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.factories.JedisFactory;

/**
 * @author Nikky-Akky
 *
 */
@Path("/v1.0/manufacturer")
@Produces({MediaType.APPLICATION_JSON})

public class ManufacturerResource {
	
	ManufacturerDetailsDAO manufacturerDetailsDao;
	ManufacturerHelperMethods manufacturerHelperMethods = new ManufacturerHelperMethods();
	Helper utilHelper = new Helper();
	HandleException exception = null;
	InvalidInputReplyClass invalidRequestReply = null;
	JSONParser parser = new JSONParser();
	JSONObject headerInputJsonData = null;
	JSONArray requiredFields;
	JedisFactory jedisCustomerAuthentication = new JedisFactory();
	Client client = ElasticFactory.getElasticClient();
	static Logger errorLog = LoggerFactory.getLogger(ManufacturerResource.class);
	
	public ManufacturerResource(ManufacturerDetailsDAO manufacturerDetailsDao){
		this.manufacturerDetailsDao = manufacturerDetailsDao;
	}
	
	
	@GET
	@Path("/getManufacturerDetails")
	public Object getManufacturerDetails(@HeaderParam("accessParam")String accessParam,@QueryParam("page")int page,@QueryParam("limit")int paginationLimit) throws ParseException, InterruptedException, ExecutionException, ConnectException{
		try{
			if(utilHelper.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = headerInputJsonData.get("apiKey").toString();
				String userName = headerInputJsonData.get("userName").toString();
				long manufacturerId = (long) headerInputJsonData.get("manufacturerId");
				requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
				/*
				 * Reducing the page number by one because of user will give page number from 1 but we will
				 * get results from page zero.
				 */
				page = page-1;
				try {
						/*
						 * validating the accesstoken given by user
						 */
					jedisCustomerAuthentication.validate(userName,accessToken, "manufacturer", "get", "getManufacturerDetails");
				} catch(Exception e) {
					e.printStackTrace();
					errorLog.info("Unautherized user "+userName+" tried to access getManufacturerDetails function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
						/*
						 * checking whether given manufacturerId is valid or not
						 */
					if(manufacturerHelperMethods.isManufacturerIdValid(manufacturerId,client)){
						if(requiredFields.size() != 0){
							String nowShowing = (page*paginationLimit+1)+"-"+(page*paginationLimit+paginationLimit);
							/*
							 * getting the required fields array given by consumer to append that in elastic search query
							 */
								SearchResponse response = null;
							Map<String,Object> manufacturerResultMap = new HashMap<String,Object>();
							List<Object> manufacturerProductsList = new ArrayList<Object>();
							List<Object> manufacturerAddressesList = new ArrayList<Object>();
							List<Object> manufacturerOrdersList = new ArrayList<Object>();
							List<Object> manufacturerInfoList = new ArrayList<Object>();
							long totalProducts = 0,totalOrders = 0,totalAddresses = 0;
								/*
								 * running the query with the parameters given by the user,getting
								 * response.
								 */
							if(requiredFields.size() != 0){
								if(requiredFields.contains("info")){
									/*
									 * query for getting info of respective manufacturer id
									 */
									BoolQueryBuilder query = QueryBuilders.boolQuery()
								            .must(QueryBuilders.termQuery("_id", manufacturerId));
									
								response = client.prepareSearch("manufacturer")
										   .setTypes("info")
										   .setQuery(query)									
										   .execute()
										   .get();	
								 SearchHit[] searchHits = response.getHits().getHits();
								 
								 for(int i=0;i<searchHits.length;i++){
									 manufacturerInfoList.add(searchHits[i].getSource());
								 }
								 manufacturerResultMap.put("ManufacturerPersonalDetails",manufacturerInfoList);
								}
								if(requiredFields.contains("products")){
									/*
									 * query for getting products of respective manufacturer id
									 */
									BoolQueryBuilder productsQuery = QueryBuilders.boolQuery()
											.must(QueryBuilders.matchQuery("manufacturerId",manufacturerId));
									
									response = client.prepareSearch("manufacturer")
											   .setTypes("products")
											   .setQuery(productsQuery)									
											   .setFrom(page*paginationLimit)
											   .setSize(paginationLimit)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 manufacturerProductsList.add(searchHits[i].getSource());
									 }								
									 totalProducts = response.getHits().getTotalHits();
									 manufacturerResultMap.put("ManufacturerProducts",manufacturerProductsList);
									}
								
								if(requiredFields.contains("addresses")){
									/*
									 * query for getting addresses of respective manufacturer id
									 */
									BoolQueryBuilder addressesQuery = QueryBuilders.boolQuery()
								            .must(QueryBuilders.matchQuery("manufacturerId", manufacturerId));
									
									response = client.prepareSearch("manufacturer")
											   .setTypes("addresses")
											   .setQuery(addressesQuery)									
											   .setFrom(page*paginationLimit)
											   .setSize(paginationLimit)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 manufacturerAddressesList.add(searchHits[i].getSource());
									 }
									 totalAddresses = response.getHits().getTotalHits();
									 manufacturerResultMap.put("ManufacturerAddresses",manufacturerAddressesList);
									}
								
								if(requiredFields.contains("orders")){
									/*
									 * query for getting orders from start date to end date of respective
									 * manufacturer id
									 */
									String startDate = headerInputJsonData.get("startDate").toString();
									String endDate = headerInputJsonData.get("endDate").toString();
									BoolQueryBuilder ordersQuery = QueryBuilders.boolQuery()
								            .must(QueryBuilders.matchQuery("manufacturerId", manufacturerId))
								            .must(QueryBuilders.rangeQuery("dateAdd").from(startDate).to(endDate));
									
									response = client.prepareSearch("manufacturer")
											   .setTypes("orders")
											   .setQuery(ordersQuery)									
											   .setFrom(page*paginationLimit)
											   .setSize(paginationLimit)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 manufacturerOrdersList.add(manufacturerHelperMethods.getOrderDetailsFromElastic((int) searchHits[i].getSource().get("orderId"),client));
									 }
									 totalOrders = response.getHits().getTotalHits();
									 manufacturerResultMap.put("ManufacturerOrders",manufacturerOrdersList);
									}
								
								return new ManufacturerPaginationReply(200,"success",totalAddresses,totalProducts,totalOrders,nowShowing,page+1,manufacturerResultMap);
								 
							}
							 else {
								 errorLog.warn("Specify your requirement in requiredFields field correctly");
								 invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify your requirement in requiredFields field correctly");
								 return invalidRequestReply;
							}
							
						} else {
							errorLog.warn("requiredFields should not be null");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "requiredFields should not be null");
							return invalidRequestReply;
						}
					} else {
						errorLog.warn("manufacturerId "+ manufacturerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "manufacturerId "+ manufacturerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
				
			} else {
				errorLog.warn("Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly");
				return invalidRequestReply;
			}
		}
		catch (NullPointerException nullPointer) {
			errorLog.warn("apiKey or manufacturerId spelled Incorrectly or mention necessary fields of address");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "apiKey or manufacturerId spelled Incorrectly or mention necessary fields of address");
			return invalidRequestReply;
		}
		catch (ParseException parse) {
			errorLog.warn("Specify your requirement in requiredFeilds as array of string");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify your requirement in requiredFeilds as array of string");
			return invalidRequestReply;
		}
		catch (ClassCastException classCast) {
			errorLog.warn("Please check the values data types");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please check the values data types");
			return invalidRequestReply;
		}
		catch (IndexNotFoundException indexNotFound) {
			errorLog.warn("Index for which you are searching is not found");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Index for which you are searching is not found");
			return invalidRequestReply;
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify date format correctly and it should not be null");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify date format correctly and it should not be null");
				return invalidRequestReply;
			} else {
				e.printStackTrace();
				errorLog.warn("Internal server connection error");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server connection error");
				return invalidRequestReply;
			}
		}
	}
}
