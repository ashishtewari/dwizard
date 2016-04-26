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

import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.helper.ManufacturerHelperMethods;
import com.mebelkart.api.util.PaginationReply;
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
	ManufacturerHelperMethods helperMethods = null;
	HandleException exception = null;
	JSONParser parser = new JSONParser();
	JSONObject headerInputJsonData = null;
	JSONArray requiredFields;
	JedisFactory jedisCustomerAuthentication = new JedisFactory();
	PaginationReply sourceResult = null;
	Client client = ElasticFactory.getElasticClient();
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	public ManufacturerResource(ManufacturerDetailsDAO manufacturerDetailsDao){
		this.manufacturerDetailsDao = manufacturerDetailsDao;
	}
	
	
	@GET
	@Path("/getManufacturerDetails")
	public Object getManufacturerDetails(@HeaderParam("accessParam")String accessParam,@QueryParam("page")int page) throws ParseException, InterruptedException, ExecutionException, ConnectException{
		try{
			helperMethods = new ManufacturerHelperMethods(manufacturerDetailsDao);
			if(helperMethods.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = (String) headerInputJsonData.get("apiKey");
				long manufacturerId = (long) headerInputJsonData.get("manufacturerId");
				String startDate = headerInputJsonData.get("startDate").toString();
				String endDate = headerInputJsonData.get("endDate").toString();
				requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
				page = page-1;
				int isUserAuthorized = jedisCustomerAuthentication.validate(accessToken, "MANUFACTURER", "GET");
				if (isUserAuthorized == 1) { // validating the accesstoken given by user
					if(helperMethods.isManufacturerIdValid(manufacturerId,client)){
						if(requiredFields.size() != 0){
							String nowShowing = (page*20+1)+"-"+(page*20+20);
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
								 * query for getting orders from start date to end date of respective
								 * manufacturer id
								 */
	
								BoolQueryBuilder query = QueryBuilders.boolQuery()
							            .must(QueryBuilders.termQuery("_id", manufacturerId));
								BoolQueryBuilder addressesQuery = QueryBuilders.boolQuery()
							            .must(QueryBuilders.matchQuery("manufacturerId", manufacturerId));
								BoolQueryBuilder productsQuery = QueryBuilders.boolQuery()
										.must(QueryBuilders.matchQuery("manufacturerId",manufacturerId));
								BoolQueryBuilder ordersQuery = QueryBuilders.boolQuery()
							            .must(QueryBuilders.matchQuery("manufacturerId", manufacturerId))
							            .must(QueryBuilders.rangeQuery("dateAdd").from(startDate).to(endDate));
								/*
								 * running the query with the parameters given by the user,getting
								 * response.
								 */
							if(requiredFields.size() != 0){
								if(requiredFields.contains("info")){
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
									response = client.prepareSearch("manufacturer")
											   .setTypes("products")
											   .setQuery(productsQuery)									
											   .setFrom(page*20)
											   .setSize(20)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 manufacturerProductsList.add(searchHits[i].getSource());
									 }								
									 totalProducts = response.getHits().getTotalHits();
									 System.out.println("total products = " + totalProducts);
									 manufacturerResultMap.put("ManufacturerProducts",manufacturerProductsList);
									}
								
								if(requiredFields.contains("addresses")){
									response = client.prepareSearch("manufacturer")
											   .setTypes("addresses")
											   .setQuery(addressesQuery)									
											   .setFrom(page*20)
											   .setSize(20)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 manufacturerAddressesList.add(searchHits[i].getSource());
									 }
									 totalAddresses = response.getHits().getTotalHits();
									 System.out.println("total addresses = " + totalAddresses);
									 manufacturerResultMap.put("ManufacturerAddresses",manufacturerAddressesList);
									}
								
								if(requiredFields.contains("orders")){
									response = client.prepareSearch("manufacturer")
											   .setTypes("orders")
											   .setQuery(ordersQuery)									
											   .setFrom(page*20)
											   .setSize(20)
											   .execute()
											   .get();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 
									 for(int i=0;i<searchHits.length;i++){
										 //manufacturerOrdersList.add(helperMethods.getOrderDetailsFromElastic((int) searchHits[i].getSource().get("orderId"),client));
										 manufacturerOrdersList.add(searchHits[i].getSource());
									 }
									 
									 totalOrders = response.getHits().getTotalHits();
									 System.out.println("total orders = " + totalOrders);
									 manufacturerResultMap.put("ManufacturerOrders",manufacturerOrdersList);
									}
								
								return new PaginationReply(200,"success",totalAddresses,totalProducts,totalOrders,nowShowing,page+1,manufacturerResultMap);
								 
							}
							 else {
								exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
								return exception.getException("Specify your requirement in requiredFields field correctly",null);
							}
							
						} else {
							exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
							return exception.getException("requiredFields should not be null",null);
						}
					} else {
						errorLog.warn("manufacturerId you mentioned was invalid");
						exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
						return exception.getException("manufacturerId "+ manufacturerId+" you mentioned was invalid",null);
					}
				} else {
					exception = new HandleException();
					return exception.accessTokenException(isUserAuthorized);
				}
			} else {
				errorLog.warn("Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly",null);
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
			return exception.getException("Specify correct data type for the values as mentioned in instructions",null);
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
		catch (ExecutionException parse) {
			errorLog.warn("please specify page value greater or equal to 1");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("please specify page value greater or equal to 1",null);
		}
		catch (Exception e) {
			if(e instanceof IllegalArgumentException){
				errorLog.warn("Specify date format correctly and it should not be null");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Specify date format correctly and it should not be null",null);
			} else {
				e.printStackTrace();
				errorLog.warn("Internal server connection error");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Internal server connection error",null);
			}
		}
	}
}
