/**
 * 
 */
package com.mebelkart.api.util.helpers;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Nikhil
 *
 */
public class Helper {
	
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
			return null;
		}
		return dataObj;
	}
	
	
	/**
	 * @return true if json is valid or false if not valid json
	 */
	public boolean isValidJson(String accessParam) {
		if(jsonParser(accessParam) == null)
			return false;
		else
			return true;
	}
	
	public String accessControlResponse(int responseStatus){
		if(responseStatus == 0)
			return "you are not a Valid User";
		else if(responseStatus == -1)
			return "You don't have Valid Access Token";
		else if(responseStatus == -2)
			return "you are not in Active State";
		else if(responseStatus == -3)
			return "You don't have Access to this Resource";
		else if(responseStatus == -4)
			return "You don't have Access to this Method";
		else if(responseStatus == -5)
			return "You don't have access to this function";
		else
			return "Your Rate Limit Exceeded";
	}

}
