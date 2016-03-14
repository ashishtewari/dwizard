/**
 * 
 */
package com.mebelkart.api.admin.v1.util;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mebelkart.api.admin.v1.api.SubStatusReply;
import com.mebelkart.api.admin.v1.api.Reply;
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

	}

	/**
	 * @param key
	 * @return
	 */
	public JSONObject jsonParser(String key) {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try {
			obj = (JSONObject) parser.parse(key);
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}
		return obj;
	}
	
	public boolean isUserDetailsValidJson(String userDetails){
		if(jsonParser(userDetails) == null)
			return false;
		else return true;
	}
	
	public boolean isUserDetailsContainsValidKeys(String userDetails){
		JSONObject obj = jsonParser(userDetails);
		if(obj.containsKey("userName") && obj.containsKey("password") && obj.containsKey("adminLevel"))
				return true;
		else
			return false;
	}

	/**
	 * @param status
	 * @return
	 */
	public Object checkStatus(int status) {
		if (status == 0) {
			return new Reply(400, "No Permissions key specified/May be misspelled",null);
		} else if (status == 1) {
			return new Reply(201, "Successfully Inserted and given Permissions",null);
		} else if (status == 2) {
			return new Reply(400, "Permissions array is Empty",null);
		} else if (status == 3) {
			return new Reply(400,"Json field in Permissions array doesn't contain resourceId/permission or misspelled",null);
		} else if (status == 4) {
			return new Reply(400, "Expected Permission array, Found Non-Array",null);
		} else if (status == 5) {
			return new Reply(400, "The values inside permission are not correctly spelled",null);
		} else if (status == 6) {
			return new Reply(201, "Successfully Updated and given Permissions",null);
		} else
			return new Reply(400, "Unknown Error in Check Status",null);
	}

	/**
	 * @param request
	 * @return
	 */
	public JSONObject contextRequest(HttpServletRequest request) {
		InputStream inputStream = null;
		String theString = null;
		try {
			inputStream = request.getInputStream();
			theString = IOUtils.toString(inputStream, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		return jsonParser(theString);
	}
	
	/**
	 * @return
	 */
	public String generateRandomAccessToken() {
		// TODO Auto-generated method stub
		return new BigInteger(130, new SecureRandom()).toString(32);
	}
	
	public String generateRandomPassword(){
		return RandomStringUtils.randomAlphanumeric(5);
	}

	/**
	 * @param email
	 * @return
	 */
	public boolean emailIsValid(String email) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		boolean valid = emailValidator.isValid(email);
		if (!valid) {
			System.out.println("E-Mail is not valid!");
		}
		return valid;
	}
}
