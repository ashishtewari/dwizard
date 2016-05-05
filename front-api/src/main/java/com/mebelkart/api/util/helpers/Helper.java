/**
 * 
 */
package com.mebelkart.api.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
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
			//e.printStackTrace();
		}
		return new Helper().jsonParser(jsonString);
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
    
    /**
     * @return the previous days date 
     */
    public String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);    
        return dateFormat.format(cal.getTime());
	}
}
