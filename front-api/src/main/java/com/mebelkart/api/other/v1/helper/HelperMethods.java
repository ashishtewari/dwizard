/**
 * 
 */
package com.mebelkart.api.other.v1.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mebelkart.api.other.v1.core.DealsWrapper;

/**
 * @author Tinku
 *
 */
public class HelperMethods {

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
		//System.out.println(products);
		for(int i = 0; i < prods.length; i++){
			DealsWrapper temp = new DealsWrapper();
			String[] prodInner = prods[i].split("\\|");
			//System.out.println(prods[i]);
			temp.setProductId(Integer.parseInt(prodInner[0]));
			temp.setProductName(prodInner[1]);
			temp.setProductImage(prodInner[2]);
			temp.setMktPrice(Integer.parseInt(prodInner[3]));
			temp.setOurPrice(Integer.parseInt(prodInner[4]));
			temp.setFlashSaleEndDate(prodInner[5]);
			temp.setFsAvailability(prodInner[6]);
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
				int pMktPrice = products.get(j).getMktPrice();
				int pOurPrice = products.get(j).getOurPrice();
				String pFlashSaleEndDate = products.get(j).getFlashSaleEndDate();
				String pFlashSaleAvailability = products.get(j).getFsAvailability();
				value = value+pId+"|"+pName+"|"+pImage+"|"+pMktPrice+"|"+pOurPrice+"|"+pFlashSaleEndDate+"|"+pFlashSaleAvailability;
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
	
	
	
}
