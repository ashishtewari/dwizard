/**
 * 
 */
package com.mebelkart.api.util.helpers;

import java.text.SimpleDateFormat;

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
    /**
     * Core Function for validation check.
     * @param dateValue
     * @return true/false
     */
    public boolean isDateValid(String dateValue)
    {
        boolean returnVal = false;
        /*
         * Set the permissible formats.
         * A better approach here would be to define all formats in a .properties file
         * and load the file during execution.
         */
        String[] permissFormats = new String[]{"yyyy/MM/dd","yyyy-MM-dd","yyyy/MM/dd hh:mm:ss","yyyy-MM-dd hh:mm:ss"};
        /*
         * Loop through array of formats and validate using JAVA API.
         */
        for (int i = 0; i < permissFormats.length; i++) 
        {
        	try 
        	{
                SimpleDateFormat sdfObj = new SimpleDateFormat(permissFormats[i]);
                sdfObj.setLenient(false);
                sdfObj.parse(dateValue);
                returnVal = true;
                System.out.println("Looks like a valid date for Date Value :"+dateValue+": For Format:"+permissFormats[i]);
                break;
        	} catch (java.text.ParseException e) {
                System.out.println("Parse Exception Occured for Date Value :"+dateValue+":And Format:"+permissFormats[i]);
			}           
        }
        return returnVal;
    }
}
