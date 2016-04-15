/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.resources;

import java.rmi.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
	@SuppressWarnings("static-access")
	Client client = new ElasticFactory().getElasticClient();
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	public ManufacturerResource(ManufacturerDetailsDAO manufacturerDetailsDao){
		this.manufacturerDetailsDao = manufacturerDetailsDao;
	}
	
	@SuppressWarnings("unused")
	@GET
	@Path("/getDetails")
	public Object getManufacturerDetails(@HeaderParam("accessParam")String accessParam,@QueryParam("page")int pageNumber) throws ParseException, InterruptedException, ExecutionException, ConnectException{
		try{
			helperMethods = new ManufacturerHelperMethods(manufacturerDetailsDao);
			if(helperMethods.isValidJson(accessParam)){
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
				String accessToken = (String) headerInputJsonData.get("apiKey");
				long manufacturerId = (long) headerInputJsonData.get("manufacturerId");
				String startDate = headerInputJsonData.get("startDate").toString();
				String endDate = headerInputJsonData.get("endDate").toString();
				requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
				int isUserAuthorized = jedisCustomerAuthentication.validate(accessToken, "MANUFACTURER", "GET");
				if (isUserAuthorized == 1) { // validating the accesstoken given by user
					if(helperMethods.isManufacturerIdValid(manufacturerId,client)){
						if(requiredFields.size() != 0){
							String nowShowing = ""+pageNumber*20+"-"+(pageNumber*20+20);
							/*
							 * getting the required fields array given by consumer to append that in elastic search query
							 */
							String consumerRequestedFieldsArray[] = helperMethods.getRequiredDetails(requiredFields);
							 List<Object>sourceResultList = new ArrayList<Object>();
							 SearchResponse response = null;
								/*
								 * query for getting orders from start date to end date of respective
								 * manufacturer id
								 */
//							
								BoolQueryBuilder query = QueryBuilders.boolQuery()
										//.must(QueryBuilders.matchQuery("manufacturerId", manufacturerId));
							            .should(QueryBuilders.termsQuery("_id", manufacturerId+""));
							            //.must(QueryBuilders.rangeQuery("manufacturerProducts.productId").from(50).to(60));
							            
								/*
								 * running the query with the parameters given by the user,getting
								 * response.
								 */
							if(consumerRequestedFieldsArray.length != 0){
								response = client.prepareSearch("mk")
										   .setTypes("manufacturer")//.setExtraSource(productsMap)
										   .setSearchType(SearchType.QUERY_AND_FETCH)
										   .setFetchSource(consumerRequestedFieldsArray,null)
										   .setQuery(query)//.setPostFilter(QueryBuilders.rangeQuery("manufacturerOrders.orderDetails.orderId").from(1).to(1))
										   //.addAggregation(AggregationBuilders.terms("mk").field("manufacturerProducts").size(1))
										   .addSort(SortBuilders.fieldSort("manufacturerProducts").order(SortOrder.DESC))
//										   .setFrom(0)
//										   .setSize(2)
										   .execute()
										   .get();	
								 SearchHit[] searchHits = response.getHits().getHits();
								 /*
								  * getting source of every hit and adding to list 
								  */
								 for(int i=0;i<searchHits.length;i++){
									 sourceResultList.add(searchHits[i].getSource());
								 }
								 
								return new PaginationReply(200,"success",response.getHits().getTotalHits(),nowShowing,pageNumber+1,sourceResultList);
								 
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
		catch (IndexNotFoundException classCast) {
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
				errorLog.warn("Internal server connection error");
				exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
				return exception.getException("Internal server connection error",null);
			}
		}
	}
}
