/**
 * 
 */
package com.mebelkart.api.cart.v1.helper;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;

import com.mebelkart.api.cart.v1.dao.CartOperationsDAO;
import com.mebelkart.api.util.factories.ElasticFactory;

/**
 * @author Nikhil
 *
 */
public class CartHelper {
	
	CartOperationsDAO cartDao;
	Client productsClient = ElasticFactory.getProductsElasticClient();;

	public CartHelper(CartOperationsDAO cartDao) {
		this.cartDao = cartDao;
	}

//	/**
//	 * @param cartId
//	 * @return total cart quantity
//	 */
//	public int getTotalCartQuantity(String cartId) {
//		int totalCartQuantity = cartDao.getCartTotalQuantity(cartId);
//		return totalCartQuantity;
//	}

	/**
	 * @param cartId
	 * @param productQuantity 
	 * @return
	 */
	public double getCartTotalPrice(String cartId) {
		List<Integer> listOfProductIdsInCart = cartDao.getAllProductsInCart(cartId);
		double totalCartPrice = 0.0;
		for(int i=0;i<listOfProductIdsInCart.size();i++){
			totalCartPrice = totalCartPrice + (getProductPrice(listOfProductIdsInCart.get(i).toString()) * cartDao.getProductQuantity(cartId, listOfProductIdsInCart.get(i).toString()));
			//System.out.println("quantity = " + cartDao.getProductQuantity(cartId, listOfProductIdsInCart.get(i).toString()));
//			System.out.println("total product price of quantity "+cartDao.getProductQuantity(cartId, listOfProductIdsInCart.get(i).toString())+" = " + (getProductPrice(listOfProductIdsInCart.get(i).toString()) * cartDao.getProductQuantity(cartId, listOfProductIdsInCart.get(i).toString())));
//			System.out.println("total cart Price = " + totalCartPrice);
		}
		return totalCartPrice;
	}

	/**
	 * @param integer
	 * @return
	 */
	public double getProductPrice(String productId) {
			GetResponse response = productsClient.prepareGet("mkproducts", "product", productId).setFields("categoryVars.price_tax_exc").execute().actionGet();
			double productPrice = Double.parseDouble(response.getField("categoryVars.price_tax_exc").getValue().toString());
//			System.out.println("single product price = " + productPrice);
		return productPrice;
	}

	/**
	 * @param productId
	 * @return
	 */
	public String getProductName(String productId) {
		GetResponse response = productsClient.prepareGet("mkproducts", "product", productId).setFields("product_name").execute().actionGet(); 
		String productName = response.getField("product_name").getValue().toString();
		return productName;
	}

	/**
	 * @param cartResultMap
	 * @param cartId
	 * @param cartQuantity
	 * @param totalCartPrice
	 * @param totalCartQuantity
	 * @param productId
	 * @param object
	 * @param productQuantity
	 * @return 
	 */
	public Map<String, Object> addResultValuesToMap(
			String cartId, Integer cartQuantity, double totalCartPrice,
			Integer totalCartQuantity, String productId, Object productName,
			Integer productQuantity) {
		Map<String,Object>cartResultMap = new HashMap<String, Object>();
		cartResultMap.put("cart_id", cartId);
		cartResultMap.put("cart_quantity", cartDao.getCartQuantity(cartId).toString());
		cartResultMap.put("total_price", totalCartPrice+"");
		cartResultMap.put("cart_total_quantity", totalCartQuantity.toString());
		cartResultMap.put("prod_id", productId);
		cartResultMap.put("prod_name", productName.toString());
		cartResultMap.put("prod_quantity", productQuantity.toString());
		return cartResultMap;
		
	}

	/**
	 * @param cartId
	 * @param addressId 
	 * @return
	 */
	public Map<String, Object> getProductDetailsInCart(String cartId, String addressId) {
		List<Integer> productIdsInCart = new ArrayList<Integer>();
		Map<String,Object> productDetails = new HashMap<String,Object>();
		List<HashMap<String,Object>> productDetailsList = new ArrayList<HashMap<String,Object>>();
		double totalCartPrice = 0.0,totalShipping = 0.0;
		String productId = ""; int productQuantity = 0;
		productDetails.put("cart_id", cartId);
		productDetails.put("cart_quantity", cartDao.getCartQuantity(cartId).toString());
		productDetails.put("cart_total_quantity",cartDao.getCartTotalQuantity(cartId));
		productDetails.put("id_address", addressId);
		
		productIdsInCart = cartDao.getAllProductsInCart(cartId);
		for(int i=0;i<productIdsInCart.size();i++){
			productId = productIdsInCart.get(i).toString();
			productQuantity = cartDao.getProductQuantity(cartId,productId);
			productDetailsList.add((HashMap<String, Object>) getProductDetailsFromElastic(cartId,productId,productQuantity));
			totalCartPrice = totalCartPrice+Double.parseDouble(productDetailsList.get(i).get("total_price").toString());
			totalShipping = totalShipping + Double.parseDouble(productDetailsList.get(i).get("shipping_charge").toString());
		}
		productDetails.put("total_cart_price",totalCartPrice);
		productDetails.put("total_shipping", totalShipping);
		productDetails.put("products",productDetailsList);
		return productDetails;
	}

	/**
	 * @param cartId 
	 * @param productQuantity 
	 * @param integer
	 * @return
	 */
	private Map<String, Object> getProductDetailsFromElastic(String cartId, String productId, int productQuantity) {
		Map<String,Object> productDetails = new HashMap<String,Object>();
		GetResponse response = productsClient.prepareGet("mkproducts", "product", productId+"")
								.setFields("product_name","info.id_image","info.reference","categoryVars.id_product_attribute",
										"categoryVars.price_tax_exc","info.id_manufacturer","location.shipped_from_days",
										"location.shipped_to_days","info.additional_shipping_cost")
								.execute()
								.actionGet();
		productDetails.put("product_id",productId);
		productDetails.put("product_image", response.getField("info.id_image").getValue().toString());
		productDetails.put("product_code", response.getField("info.reference").getValue().toString());
		productDetails.put("product_attribute", response.getField("categoryVars.id_product_attribute").getValue().toString());
		productDetails.put("product_name", response.getField("product_name").getValue().toString());
		productDetails.put("price",response.getField("categoryVars.price_tax_exc").getValue().toString());
		productDetails.put("quantity",productQuantity+"");
		productDetails.put("total_price",((long)response.getField("categoryVars.price_tax_exc").getValue()*productQuantity)+"");
		productDetails.put("seller", response.getField("info.id_manufacturer").getValue().toString());
		productDetails.put("delivery_date","Dispatched in "+response.getField("location.shipped_from_days").getValue()+"-"+response.getField("location.shipped_from_days").getValue()+" working days");
		productDetails.put("shipping_charge",response.getField("info.additional_shipping_cost").getValue());
		
		return productDetails;
	}
	
}
