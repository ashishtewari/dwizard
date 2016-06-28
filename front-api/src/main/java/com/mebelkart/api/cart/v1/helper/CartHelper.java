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

	/**
	 * @param cartId
	 * @return total cart quantity
	 */
	public int getTotalCartQuantity(String cartId) {
		int totalCartQuantity = cartDao.getCartTotalQuantity(cartId);
		return totalCartQuantity;
	}

	/**
	 * @param cartId
	 * @param productQuantity 
	 * @return
	 */
	public double getCartTotalPrice(String cartId, int productQuantity) {
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
			GetResponse response = productsClient.prepareGet("mkproducts", "product", productId).setFields("info.price").execute().actionGet();
			double productPrice = Double.parseDouble((String) response.getField("info.price").getValue());
//			System.out.println("single product price = " + productPrice);
		return productPrice;
	}

	/**
	 * @param productId
	 * @return
	 */
	public List<Object> getProductDetails(String productId) {
		List<Object>productDetailsList = new ArrayList<Object>();
		GetResponse response = productsClient.prepareGet("mkproducts", "product", productId).setFields("product_name").execute().actionGet(); 
		productDetailsList.add((String) response.getField("product_name").getValue());
		return productDetailsList;
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
		cartResultMap.put("cartId", cartId);
		cartResultMap.put("cartQuantity", cartDao.getCartQuantity(cartId).toString());
		cartResultMap.put("totalPrice", totalCartPrice+"");
		cartResultMap.put("cartTotalQuantity", totalCartQuantity.toString());
		cartResultMap.put("productId", productId);
		cartResultMap.put("productName", productName.toString());
		cartResultMap.put("productQuantity", productQuantity.toString());
		return cartResultMap;
		
	}

	
}
