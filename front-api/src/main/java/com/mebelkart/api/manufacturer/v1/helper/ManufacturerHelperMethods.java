/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.helper;

import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mebelkart.api.manufacturer.v1.core.ManufacturerDetailsWrapper;
import com.mebelkart.api.manufacturer.v1.dao.ManufacturerDetailsDAO;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerHelperMethods {
	
	
	ManufacturerDetailsDAO manufacturerDetailsDao;
	public ManufacturerHelperMethods(ManufacturerDetailsDAO manufacturerDetailsDao) {
		this.manufacturerDetailsDao = manufacturerDetailsDao;
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
	 * @param manufacturerId 
	 * @return
	 */
	public boolean isManufacturerIdValid(long manufacturerId) {
		List<ManufacturerDetailsWrapper> manufacturerIdList = this.manufacturerDetailsDao.getManufacturerId(manufacturerId);
		if(manufacturerIdList.size()>0){
			return true;
		}
		return false;
	}
}
