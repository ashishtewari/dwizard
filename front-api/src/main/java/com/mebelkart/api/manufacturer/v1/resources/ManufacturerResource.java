/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.resources;

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
import org.elasticsearch.client.Client;
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
import com.mebelkart.api.manufacturer.v1.core.ManufacturerOrders;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.helper.ManufacturerHelperMethods;
import com.mebelkart.api.util.PaginationReply;
import com.mebelkart.api.util.Reply;
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
	
	@GET
	@Path("/getDetails")
	public Reply getManufacturerDetails(@HeaderParam("accessParam")String accessParam,@QueryParam("page")int pageNumber) throws ParseException, InterruptedException, ExecutionException{
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
							if(requiredFields.contains("orders")){
								/*
								 * query for getting orders from start date to end date of respective
								 * manufacturer id
								 */
								BoolQueryBuilder query = QueryBuilders.boolQuery()
							            .must(QueryBuilders.matchQuery("id_manufacturer", manufacturerId))
							            .must(QueryBuilders.rangeQuery("date_add").from(startDate).to(endDate));
								/*
								 * running the query with the parameters given by the user,getting
								 * response with the pagination.
								 */
								SearchResponse response = client.prepareSearch("orders")
						                .setQuery(query).setFrom((int) (pageNumber*20)).setSize(20).execute()
						                .actionGet();	
								 SearchHit[] searchHits = response.getHits().getHits();
								 List<ManufacturerOrders>sourceResultList = new ArrayList<ManufacturerOrders>();
								 /*
								  * converting every single object into camelCase and adding to the list 
								  */
								 for(int i=0;i<searchHits.length;i++){
									 sourceResultList.add(helperMethods.changeOrdersToCamelCase(searchHits[i].getSource()));
								 }
								 sourceResult = new PaginationReply(response.getHits().getTotalHits(),nowShowing,pageNumber+1,sourceResultList);
								 return new Reply(200,"success",sourceResult);
								 
							} else
								if(requiredFields.contains("products")) {
									/*
									 * query for getting orders of respective manufacturer id
									 */
									BoolQueryBuilder query = QueryBuilders.boolQuery()
								            .must(QueryBuilders.matchQuery("id_manufacturer", manufacturerId));
									/*
									 * running the query with the parameters given by the user,getting
									 * response with the pagination.
									 */
									SearchResponse response = client.prepareSearch("orders")
							                .setQuery(query).setFrom((int) (pageNumber*20)).setSize(20).execute()
							                .actionGet();	
									 SearchHit[] searchHits = response.getHits().getHits();
									 List<Object>sourceResultList = new ArrayList<Object>();
									 /*
									  * adding every single source to the list 
									  */
									 for(int i=0;i<searchHits.length;i++){
										 sourceResultList.add(searchHits[i].getSource());
									 }
									 PaginationReply sourceResult = new PaginationReply(response.getHits().getTotalHits(),nowShowing,pageNumber+1,sourceResultList);
									 return new Reply(200,"success",sourceResult);
									 
							} else {
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
		catch (Exception illegalArgument) {
			errorLog.warn("Specify date format correctly and it should not be null");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Specify date format correctly and it should not be null",null);
		}
	}
}
