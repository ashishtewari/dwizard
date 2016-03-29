package com.mebelkart.api.admin.v1.helper;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mebelkart.api.util.HandleException;
import com.mebelkart.api.admin.v1.dao.AdminDAO;

/**
 * @author Tinku
 *
 */
public class HelperMethods {
	
	/**
	 * auth
	 */
	AdminDAO auth;
	
	/**
	 * Default Constructer
	 */
	public HelperMethods() {
		this.auth = null;
	}

	/**
	 * This method parse Json string to JsonObject
	 * @param data of JsonString
	 * @return JSONObject
	 */
	public JSONObject jsonParser(String data) {
		JSONParser parser = new JSONParser();
		JSONObject dataObj = null;
		try {
			dataObj = (JSONObject) parser.parse(data);
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
		return dataObj;
	}
	
	/**
	 * This method checks whether the JsonString provided is in valid Json format or not
	 * @param userDetails of JsonString
	 * @return true/false
	 */
	public boolean isUserDetailsValidJson(String userDetails){
		try {
	        new JsonParser().parse(userDetails);
	        return true;
	    } catch (JsonSyntaxException e) {
	        return false;
	    }
	}
	
	/**
	 * Checks whether all the keys provided in JsonString are valid or not
	 * @param userDetails of JsonString
	 * @return true/false
	 */
	public boolean isUserDetailsContainsValidKeys(String userDetails){
		JSONObject userDetailsObj = jsonParser(userDetails);
		if(userDetailsObj.containsKey("userName") && userDetailsObj.containsKey("password"))
				return true;
		else
			return false;
	}

	/**
	 * It returns HTTPStatus based on status
	 * @param status of type int
	 * @param msg may be passed/Not passed
	 * @return Object
	 */
	public Object checkStatus(int status) {
		HandleException exception = null;
		if (status == 0) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("No Permissions key specified/May be misspelled", null);
		} else if (status == 2) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Permissions array is Empty", null);
		} else if (status == 3) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Json field in Permissions array doesn't contain resourceId/permission or misspelled", null);
		} else if (status == 4) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Expected Permission array, Found Non-Array", null);
		} else if (status == 5) {
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("The values inside permission are not correctly spelled", null);
		} else{
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			return exception.getException("Unknown Error in Check Status", null);
		}
	}

	/**
	 * It converts servlet request of type Json to JsonString
	 * @param request consists of body data of type raw json data
	 * @return JSONObject
	 */
	public JSONObject contextRequestParser(HttpServletRequest request) {
		InputStream inputStream = null;
		String jsonString = null;
		try {
			inputStream = request.getInputStream();
			jsonString = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return jsonParser(jsonString);
	}
	
	/**
	 * This method generates unique access tokens
	 * @return token of type string
	 */
	public String generateUniqueAccessToken() {
		// TODO Auto-generated method stub
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	/**
	 * Generates random password
	 * @return password of type string
	 */
	public String generateRandomPassword(){
		return RandomStringUtils.randomAlphanumeric(5);
	}

	/**
	 * Checks whether String is of type email
	 * @param email
	 * @return true/false
	 */
	public boolean emailIsValid(String email) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		boolean valid = emailValidator.isValid(email);
		if (!valid) {
			System.out.println("E-Mail is not valid!");
		}
		return valid;
	}
	
	public String currentTimeStamp()
    {
	 Date date= new java.util.Date();
	 String[] time =  new Timestamp(date.getTime()).toString().split("\\.");
	 return time[0];
    }
}
