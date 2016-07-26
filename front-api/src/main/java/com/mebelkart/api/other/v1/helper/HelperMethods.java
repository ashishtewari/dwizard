/**
 * 
 */
package com.mebelkart.api.other.v1.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mebelkart.api.other.v1.core.DealsWrapper;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
public class HelperMethods {

	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	/**
	 * @param categoriesWithProductDetails
	 * @return
	 */
	public Object getDealsPage(Map<String, String> categoriesWithProductDetails) {
		List<Object> results = new ArrayList<Object>();		
		Set<String> catNameIds = categoriesWithProductDetails.keySet();
		for(String cat: catNameIds ){
			Map<String,Object> temp = new HashMap<String,Object>();
			temp.put("categoryId",Integer.parseInt(cat.split("\\|\\|")[0]));
			temp.put("catName", cat.split("\\|\\|")[1]);
			temp.put("products",getProducts(categoriesWithProductDetails.get(cat)));
			results.add(temp);
		}
		return results;
	}

	/**
	 * @param string
	 * @return
	 */
	private List<DealsWrapper> getProducts(String products) {
		List<DealsWrapper> prod = new ArrayList<DealsWrapper>();
		String[] prods = products.split("\\|\\|");
		for(int i = 0; i < prods.length; i++){
			DealsWrapper temp = new DealsWrapper();
			String[] prodInner = prods[i].split("\\|");
			temp.setProductId(Integer.parseInt(prodInner[0]));
			temp.setProductName(prodInner[1]);
			temp.setProductImage(prodInner[2]);
			temp.setCatId(Integer.parseInt(prodInner[3]));
			temp.setMktPrice(Integer.parseInt(prodInner[4]));
			temp.setOurPrice(Integer.parseInt(prodInner[5]));
			temp.setFlashSaleEndDate(prodInner[6]);
			temp.setFlashSaleEndsIn(getRemainingTime(helper.getCurrentDateString(),prodInner[6]));
			temp.setFsAvailability(prodInner[7]);
			prod.add(temp);
		}
		return prod;
	}

	/**
	 * @param results
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String,String> convertDealsPageDataIntoMap(List<Object> results) {
		Map<String,String> dealsPageData = new HashMap<String,String>();
		for(int i = 0; i < results.size(); i++){
			String field = "";
			String value = "";
			String catName = ((Map<String,Object>) results.get(i)).get("catName")+"";
			String catId = ((Map<String,Object>) results.get(i)).get("categoryId")+"";
			List<DealsWrapper> products = (List<DealsWrapper>) ((Map<String,Object>) results.get(i)).get("products");
			for(int j = 0; j < products.size(); j++){
				int pId = products.get(j).getProductId();
				String pName = products.get(j).getProductName();
				String pImage = products.get(j).getProductImage();
				int catagoryId = products.get(j).getCatId();
				int pMktPrice = products.get(j).getMktPrice();
				int pOurPrice = products.get(j).getOurPrice();
				String pFlashSaleEndDate = products.get(j).getFlashSaleEndDate();
				String pFlashSaleAvailability = products.get(j).getFsAvailability();
				value = value+pId+"|"+pName+"|"+pImage+"|"+catagoryId+"|"+pMktPrice+"|"+pOurPrice+"|"+pFlashSaleEndDate+"|"+pFlashSaleAvailability;
				if(j == products.size() - 1){					
				}else{
					value = value+"||";
				}
			}
			field = catId+"||"+catName;
			dealsPageData.put(field, value);
		}
		return dealsPageData;
	}

	/**
	 * @param currentDateString
	 * @param string
	 * @return
	 */
	public String getRemainingTime(String currentDate, String endDate) {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1;
		Date date2;
		try {
			date1 = simpleDateFormat.parse(currentDate);
			date2 = simpleDateFormat.parse(endDate);
			//milliseconds
			long different = date2.getTime() - date1.getTime();
			
			if(different < 0)
				return "0d 0h 0m";
			
			long secondsInMilli = 1000;
			long minutesInMilli = secondsInMilli * 60;
			long hoursInMilli = minutesInMilli * 60;
			long daysInMilli = hoursInMilli * 24;

			long elapsedDays = different / daysInMilli;
			different = different % daysInMilli;
			
			long elapsedHours = different / hoursInMilli;
			different = different % hoursInMilli;
			
			long elapsedMinutes = different / minutesInMilli;
			different = different % minutesInMilli;
			
			return elapsedDays+"d "+elapsedHours+"h "+elapsedMinutes+"m";
		} catch (ParseException e) {
			return "0d 0h 0m";
		}
	}
	
	
	
}
