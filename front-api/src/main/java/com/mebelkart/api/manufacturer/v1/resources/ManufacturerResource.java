/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.resources;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.manufacturer.v1.core.ManufacturerDetailsWrapper;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;
import com.mebelkart.api.manufacturer.v1.helper.ManufacturerHelperMethods;
import com.mebelkart.api.util.Reply;
import com.mebelkart.api.util.exceptions.HandleException;
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
	MD5Encoding encode = new MD5Encoding();
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	public ManufacturerResource(ManufacturerDetailsDAO manufacturerDetailsDao){
		this.manufacturerDetailsDao = manufacturerDetailsDao;
	}
	
	@GET
	@Path("/getDetails")
	public Reply getManufacturerDetails(@HeaderParam("accessParam")String accessParam) throws ParseException{
		helperMethods = new ManufacturerHelperMethods(manufacturerDetailsDao);
		if(helperMethods.isValidJson(accessParam)){
			headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values 
			String accessToken = (String) headerInputJsonData.get("apiKey");
			long manufacturerId = (long) headerInputJsonData.get("manufacturerId");
			requiredFields =  (JSONArray)headerInputJsonData.get("requiredFields");
			System.out.println("manufacturerID = " + manufacturerId);
			if (jedisCustomerAuthentication.validate(accessToken, "CUSTOMER", "GET") == 1) { // validating the accesstoken given by user
				System.out.println("one");
				if(helperMethods.isManufacturerIdValid(manufacturerId)){
					System.out.println("two");
					List<ManufacturerDetailsWrapper> manufacturerDetailsResultSet;
					if(requiredFields.size() == 0){
						System.out.println("three");
						manufacturerDetailsResultSet = manufacturerDetailsDao.getManufacturerDetails(manufacturerId);
						return new Reply(200,"success",manufacturerDetailsResultSet);
					} else {
						System.out.println("entered in else part");
						return null;
					}
				} else {
					errorLog.warn("manufacturerId you mentioned was invalid");
					exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
					return exception.getException("manufacturerId "+ manufacturerId+" you mentioned was invalid",null);
				}
			} else {
				exception = new HandleException();
				return exception.accessTokenException(jedisCustomerAuthentication.validate(accessToken, "CUSTOMER", "GET"));
			}
		}else{
			errorLog.warn("Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly");
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Content-Type or apiKey or manufacturerId or requiredFields spelled Incorrectly",null);
		}
		
	}
}
