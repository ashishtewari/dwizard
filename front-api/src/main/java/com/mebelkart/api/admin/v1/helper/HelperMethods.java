package com.mebelkart.api.admin.v1.helper;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.elasticsearch.common.Base64;
import org.json.simple.JSONObject;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.helpers.Helper;
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
	 * InvalidInputReplyClass class
	 */
	InvalidInputReplyClass invalidRequestReply = null;
	
	/**
	 * Default Constructer
	 */
	public HelperMethods() {
		this.auth = null;
	}
	
	/**
	 * Checks whether all the keys provided in JsonString are valid or not
	 * @param userDetails of JsonString
	 * @return true/false
	 */
	public boolean isUserDetailsContainsValidKeys(String userDetails){
		JSONObject userDetailsObj = new Helper().jsonParser(userDetails);
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
		if (status == 0) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"No Permissions key specified/May be misspelled");
			return invalidRequestReply;
		} else if (status == 2) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"Permissions array is Empty");
			return invalidRequestReply;
		} else if (status == 3) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"Json field in Permissions array doesn't contain resourceId/permission or misspelled");
			return invalidRequestReply;
		} else if (status == 4) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"Expected Permission/Function array, Found Non-Array");
			return invalidRequestReply;
		} else if (status == 5) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"The values inside permission are not correctly spelled");
			return invalidRequestReply;
		} else if(status == 7){
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"give valid function keys");
			return invalidRequestReply;
		}else if (status == -1) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"you can't give consumer access to ADMIN resource and so avoided this permission");
			return invalidRequestReply;
		} else if (status == -2) {
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(),"but not given permissions to some of the resources");
			return invalidRequestReply;
		}else{
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(),"Unknown Error in Check Status");
			return invalidRequestReply;
		}
	}
	
	/**
	 * 
	 */
	public String generateSessionId(){
		String temp = RandomStringUtils.randomAlphanumeric(16);
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.DATE, -1);  
        return temp+dateFormat.format(cal.getTime());
	}
	
	/**
	 * This method generates unique access tokens
	 * @return token of type string
	 */
	public String generateUniqueAccessToken() {
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
	
	/**
	 * This function returns the current time stamp
	 * @return
	 */
	public String currentTimeStamp()
    {
	 Date date= new java.util.Date();
	 String[] time =  new Timestamp(date.getTime()).toString().split("\\.");
	 return time[0];
    }

	/**
	 * This function decodes base64 encoded value 
	 * @param apikey
	 * @return
	 */
	public String getBase64Decoded(String apikey) {
		byte[] valueDecoded = null;
		try {
			valueDecoded = Base64.decode(apikey );
		} catch (IOException e) {
			System.out.println("Exception occurred in base64 decoding");
		}
		return new String(valueDecoded);
	}
}
