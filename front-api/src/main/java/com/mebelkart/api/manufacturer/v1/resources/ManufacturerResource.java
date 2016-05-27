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
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.get.GetResponse;
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

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.helper.ManufacturerHelperMethods;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.PaginationReply;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;

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
	InvalidInputReplyClass invalidRequestReply = null;
	JSONParser parser = new JSONParser();
	JSONObject headerInputJsonData = null;
	JSONArray requiredFields;
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();
	//JedisFactory jedisCustomerAuthentication = new JedisFactory();
	Client client = ElasticFactory.getElasticClient();
	Client productsClient = ElasticFactory.getProductsElasticClient();
	static Logger errorLog = LoggerFactory.getLogger(ManufacturerResource.class);
	
	public ManufacturerResource(ManufacturerDetailsDAO manufacturerDetailsDao){
		this.manufacturerDetailsDao = manufacturerDetailsDao;
	}
	
	
	@GET
	@Path("/{id}")
	@Timed
	public Object getManufacturerDetails(@HeaderParam("accessParam")String accessParam,@PathParam("id")long manufacturerId) throws ParseException, InterruptedException, ExecutionException, ConnectException{
		try{
			if(utilHelper.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
				try {
						/*
						 * validating the accesstoken given by user
						 */
					authenticate.validate(userName,accessToken, "manufacturer", "get", "getManufacturerInfo");
				} catch(Exception e) {
					e.printStackTrace();
					errorLog.info("Unautherized user "+userName+" tried to access getManufacturerInfo function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
						/*
						 * checking whether given manufacturerId is valid or not
						 */
					if(manufacturerHelperMethods.isManufacturerIdValid(manufacturerId,client)){
							/*
							 * getting the required fields array given by consumer to append that in elastic search query
							 */
							SearchResponse response = null;
							List<Object> manufacturerInfoList = new ArrayList<Object>();
									/*
									 * query for getting info of respective manufacturer id
									 */
									BoolQueryBuilder query = QueryBuilders.boolQuery()
								            .must(QueryBuilders.termQuery("_id", manufacturerId));
									
								response = client.prepareSearch("mkmanufacturer")
										   .setTypes("manufacturerInfo")
										   .setQuery(query)									
										   .execute()
										   .get();	
								 SearchHit[] searchHits = response.getHits().getHits();
								 
								 for(int i=0;i<searchHits.length;i++){
									 manufacturerInfoList.add(searchHits[i].getSource());
								 }
								return new Reply(200,"success",manufacturerInfoList);
					} else {
						errorLog.warn("manufacturerId "+ manufacturerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "manufacturerId "+ manufacturerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
				
			} else {
				errorLog.warn("Given header is not a valid json");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Given header is not a valid json");
				return invalidRequestReply;
			}
		}
		catch (NullPointerException nullPointer) {
			errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields of address");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or manufacturerId spelled Incorrectly or mention necessary fields of address");
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
			e.printStackTrace();
			errorLog.warn("Internal server connection error");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server connection error");
			return invalidRequestReply;
		}
	}
	
	
	@GET
	@Path("/products/{id}")
	public Object getManufacturerProducts(@HeaderParam("accessParam")String accessParam,@PathParam("id")long manufacturerId,@QueryParam("page")int page,@QueryParam("limit")int paginationLimit){
		try{
			if(utilHelper.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
				try {
						/*
						 * validating the accesstoken given by user
						 */
					authenticate.validate(userName,accessToken, "manufacturer", "get", "getManufacturerProducts");
				} catch(Exception e) {
					e.printStackTrace();
					errorLog.info("Unautherized user "+userName+" tried to access getManufacturerProducts function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if((paginationLimit > 0) && (page > 0)){
					/*
					 * Reducing the page number by one because of user will give page number from 1 but we will
					 * get results from page zero.
					 */
					page = page-1;
						/*
						 * checking whether given manufacturerId is valid or not
						 */
					if(manufacturerHelperMethods.isManufacturerIdValid(manufacturerId,client)){
						String nowShowing = (page*paginationLimit+1)+"-"+(page*paginationLimit+paginationLimit);
						/*
						 * getting the required fields array given by consumer to append that in elastic search query
						 */
						SearchResponse response = null;
						Map<String,Object> manufacturerResultMap = new HashMap<String,Object>();
						List<Object> manufacturerProductsList = new ArrayList<Object>();
						List<Object> manufacturerInfoList = new ArrayList<Object>();
						long totalProducts = 0,totalPages=0;
						
								/*
								 * query for getting info of respective manufacturer id
								 */
								BoolQueryBuilder query = QueryBuilders.boolQuery()
							            .must(QueryBuilders.termQuery("_id", manufacturerId));
								
							response = client.prepareSearch("mkmanufacturer")
									   .setTypes("manufacturerInfo")
									   .setQuery(query)									
									   .execute()
									   .get();	
							 SearchHit[] searchHits = response.getHits().getHits();
								 manufacturerInfoList.add(searchHits[0].getSource());
							 manufacturerResultMap.put("ManufacturerInfo",manufacturerInfoList);

								/*
								 * query for getting products of respective manufacturer id
								 */
								BoolQueryBuilder productsQuery = QueryBuilders.boolQuery()
										.must(QueryBuilders.matchQuery("info.id_manufacturer",manufacturerId));
								
								response = productsClient.prepareSearch("mkproducts")
										   .setTypes("product")
										   .setQuery(productsQuery)									
										   .setFrom(page*paginationLimit)
										   .setSize(paginationLimit)
										   .execute()
										   .get();	
								 SearchHit[] productsSearchHits = response.getHits().getHits();
								 
								 for(int i=0;i<productsSearchHits.length;i++){
									 manufacturerProductsList.add(productsSearchHits[i].getSource());
								 }								
								 totalProducts = response.getHits().getTotalHits();
								 totalPages = totalProducts/paginationLimit;
								 manufacturerResultMap.put("ManufacturerProducts",manufacturerProductsList);
							
							return new PaginationReply(200,"success",totalProducts,totalPages,page+1,nowShowing,manufacturerResultMap);
							
						} else {
							errorLog.warn("manufacturerId "+ manufacturerId+" you mentioned was invalid");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "manufacturerId "+ manufacturerId+" you mentioned was invalid");
							return invalidRequestReply;
						}
					} else {
						errorLog.warn("Specify values of limit and page greater than 0");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify values of limit and page greater than 0");
						return invalidRequestReply;
					}
				} else {
					errorLog.warn("Given header is not a valid json");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Given header is not a valid json");
					return invalidRequestReply;
				}
			}
			catch (NullPointerException nullPointer) {
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields of address");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or manufacturerId spelled Incorrectly or mention necessary fields of address");
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
	
	
	
	@GET
	@Path("/addresses/{id}")
	public Object getManufacturerAddresses(@HeaderParam("accessParam")String accessParam,@PathParam("id")long manufacturerId){
		try{
			if(utilHelper.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
				try {
						/*
						 * validating the accesstoken given by user
						 */
					authenticate.validate(userName,accessToken, "manufacturer", "get", "getManufacturerAddresses");
				} catch(Exception e) {
					e.printStackTrace();
					errorLog.info("Unautherized user "+userName+" tried to access getManufacturerAddresses function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
						/*
						 * checking whether given manufacturerId is valid or not
						 */
					if(manufacturerHelperMethods.isManufacturerIdValid(manufacturerId,client)){
						/*
						 * getting the required fields array given by consumer to append that in elastic search query
						 */
						SearchResponse response = null;
						Map<String,Object> manufacturerResultMap = new HashMap<String,Object>();
						List<Object> manufacturerAddressesList = new ArrayList<Object>();
						List<Object> manufacturerInfoList = new ArrayList<Object>();
								/*
								 * query for getting info of respective manufacturer id
								 */
								BoolQueryBuilder query = QueryBuilders.boolQuery()
							            .must(QueryBuilders.termQuery("_id", manufacturerId));
								
							response = client.prepareSearch("mkmanufacturer")
									   .setTypes("manufacturerInfo")
									   .setQuery(query)									
									   .execute()
									   .get();	
							 SearchHit[] searchHits = response.getHits().getHits();
								 manufacturerInfoList.add(searchHits[0].getSource());
							 manufacturerResultMap.put("ManufacturerInfo",manufacturerInfoList);

								/*
								 * query for getting products of respective manufacturer id
								 */
								BoolQueryBuilder addressQuery = QueryBuilders.boolQuery()
										.must(QueryBuilders.matchQuery("manufacturerId",manufacturerId));
								
								response = client.prepareSearch("mkmanufacturer")
										   .setTypes("manufacturerAddresses")
										   .setQuery(addressQuery)									
										   .execute()
										   .get();	
								 SearchHit[] addressSearchHits = response.getHits().getHits();
								 
								 for(int i=0;i<addressSearchHits.length;i++){
									 manufacturerAddressesList.add(addressSearchHits[i].getSource());
								 }								
								 manufacturerResultMap.put("ManufacturerAddresses",manufacturerAddressesList);
							
							return new Reply(200,"success",manufacturerResultMap);
							
					} else {
						errorLog.warn("manufacturerId "+ manufacturerId+" you mentioned was invalid");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "manufacturerId "+ manufacturerId+" you mentioned was invalid");
						return invalidRequestReply;
					}
				} else {
					errorLog.warn("Given header is not a valid json");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Given header is not a valid json");
					return invalidRequestReply;
				}
			}
			catch (NullPointerException nullPointer) {
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields of address");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or manufacturerId spelled Incorrectly or mention necessary fields of address");
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
				e.printStackTrace();
				errorLog.warn("Internal server connection error");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server connection error");
				return invalidRequestReply;
			}		
		}
	
	
	
	@GET
	@Path("/orders/{id}")
	public Object getManufacturerOrders(@HeaderParam("accessParam")String accessParam,@PathParam("id")long manufacturerId,@QueryParam("page")int page,@QueryParam("limit")int paginationLimit){
		try{
			if(utilHelper.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = headerInputJsonData.get("accessToken").toString();
				String userName = headerInputJsonData.get("userName").toString();
				try {
						/*
						 * validating the accesstoken given by user
						 */
					authenticate.validate(userName,accessToken, "manufacturer", "get", "getManufacturerOrders");
				} catch(Exception e) {
					e.printStackTrace();
					errorLog.info("Unautherized user "+userName+" tried to access getManufacturerOrders function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
					return invalidRequestReply;
				}
				if((paginationLimit > 0) && (page > 0)){
					/*
					 * Reducing the page number by one because of user will give page number from 1 but we will
					 * get results from page zero.
					 */
					page = page-1;
						/*
						 * checking whether given manufacturerId is valid or not
						 */
					if(manufacturerHelperMethods.isManufacturerIdValid(manufacturerId,client)){
						String nowShowing = (page*paginationLimit+1)+"-"+(page*paginationLimit+paginationLimit);
						/*
						 * getting the required fields array given by consumer to append that in elastic search query
						 */
						SearchResponse response = null;
						Map<String,Object> manufacturerResultMap = new HashMap<String,Object>();
						List<Object> manufacturerOrdersList = new ArrayList<Object>();
						List<Object> manufacturerInfoList = new ArrayList<Object>();
						long totalOrders = 0,totalPages = 0;
						
								/*
								 * query for getting info of respective manufacturer id
								 */
//								BoolQueryBuilder query = QueryBuilders.boolQuery()
//							            .must(QueryBuilders.termQuery("_id", manufacturerId));
								
//							response = client.prepareSearch("mkmanufacturer")
//									   .setTypes("manufactuerAddresses")
//									   .setQuery(query)									
//									   .execute()
//									   .get();
							GetResponse getResponse = client.prepareGet("mkmanufacturer","manufacturerInfo",manufacturerId+"")
									.execute().actionGet();
							 //SearchHit[] manufacturerInfoSearchHits = response.getHits().getHits();
//							 System.out.println("hits = " + response.getHits().getTotalHits());
//							 System.out.println("size = " + manufacturerInfoSearchHits.length);
							 //for(int i=0;i<manufacturerInfoSearchHits.length;i++){
								 manufacturerInfoList.add(getResponse.getSource());
							 //}
							 manufacturerResultMap.put("ManufacturerInfo",manufacturerInfoList);

							 	/*
								 * query for getting orders from start date to end date of respective
								 * manufacturer id
								 */
								String startDate = headerInputJsonData.get("startDate").toString();
								String endDate = headerInputJsonData.get("endDate").toString();
								BoolQueryBuilder ordersQuery = QueryBuilders.boolQuery()
							            .must(QueryBuilders.matchQuery("manufacturerId", manufacturerId))
							            .must(QueryBuilders.rangeQuery("dateAdd").from(startDate).to(endDate));
								
								response = client.prepareSearch("mkmanufacturer")
										   .setTypes("manufacturerOrders")
										   .setQuery(ordersQuery)									
										   .setFrom(page*paginationLimit)
										   .setSize(paginationLimit)
										   .execute()
										   .get();	
								 SearchHit[] ordersSearchHits = response.getHits().getHits();
								 
								 for(int i=0;i<ordersSearchHits.length;i++){
									 manufacturerOrdersList.add(manufacturerHelperMethods.getOrderDetailsFromElastic((int) ordersSearchHits[i].getSource().get("orderId"),client));
								 }
								 totalOrders = response.getHits().getTotalHits();
								 totalPages = totalOrders/paginationLimit;
								 manufacturerResultMap.put("ManufacturerOrders",manufacturerOrdersList);
							return new PaginationReply(200,"success",totalOrders,totalPages,page+1,nowShowing,manufacturerResultMap);
							
						} else {
							errorLog.warn("manufacturerId "+ manufacturerId+" you mentioned was invalid");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "manufacturerId "+ manufacturerId+" you mentioned was invalid");
							return invalidRequestReply;
						}
					} else {
						errorLog.warn("Specify values of limit and page greater than 0");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify values of limit and page greater than 0");
						return invalidRequestReply;
					}
				} else {
					errorLog.warn("Given header is not a valid json");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Given header is not a valid json");
					return invalidRequestReply;
				}
			}
			catch (NullPointerException nullPointer) {
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields of address");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields of address");
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
