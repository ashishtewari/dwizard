/**
 * 
 */
package com.mebelkart.api.cart.v1.resources;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.elasticsearch.client.Client;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.cart.v1.core.CartProductDetailsWrapper;
import com.mebelkart.api.cart.v1.dao.CartOperationsDAO;
import com.mebelkart.api.cart.v1.helper.CartHelper;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Nikhil
 *
 */

@Path("/v1.0/cart")
public class CartResource {
	
	InvalidInputReplyClass invalidRequestReply = null;
	JSONParser parser = new JSONParser();
	JSONObject headerInputJsonData = null,bodyInputJsonData = null;
	Authentication authenticate = new Authentication();
	Helper utilHelper = new Helper();
	CartOperationsDAO cartDao;
	CartHelper cartHelper = null;
	Client productsClient;
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	/**
	 * @param cartOperationsDao
	 */
	public CartResource(CartOperationsDAO cartOperationsDao) {
		// TODO Auto-generated constructor stub
		this.cartDao = cartOperationsDao;
	}

	@SuppressWarnings("unused")
	@POST
	@Path("/addProduct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Timed
	public Object addProductToCart(@HeaderParam("accessParam")String accessParam,@Context HttpServletRequest request){
		
		try {
			cartHelper = new CartHelper(cartDao);	
			if(utilHelper.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
				bodyInputJsonData = utilHelper.contextRequestParser(request);
				if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
					String accessToken = (String) headerInputJsonData.get("accessToken");
					String userName = (String) headerInputJsonData.get("userName");
					try {
							/*
							 * validating the accesstoken given by user
							 */
						authenticate.validate(userName,accessToken, "customer", "get","addProductToCart");
						
					} catch(Exception e) {
//						e.printStackTrace();
						errorLog.warn("Unautherized user "+userName+" tried to access addProduct function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(bodyInputJsonData.containsKey("id_product") && bodyInputJsonData.containsKey("id_product_attribute") && bodyInputJsonData.containsKey("id_customer") && bodyInputJsonData.containsKey("secure_key") && bodyInputJsonData.containsKey("id_cart") && bodyInputJsonData.containsKey("id_guest")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String productId = bodyInputJsonData.get("id_product").toString();
						String productAttributeId = bodyInputJsonData.get("id_product_attribute").toString();
						String customerId = bodyInputJsonData.get("id_customer").toString();
						String secureKey = bodyInputJsonData.get("secure_key").toString();
						String guestId = bodyInputJsonData.get("id_guest").toString();
						System.out.println("cartId = " + cartId);
						
						int isProductAddedToCart = 0,totalCartQuantity = 0,updateProductQuantityInCart = 0,productQuantity = 0,isCartCreated = 0;
						int langId = 1, currencyId = 4,carrierId = 0;
						double totalCartPrice = 0.0;
						System.out.println((!cartId.equals("0")));
						System.out.println((!cartId.equals("")));
						System.out.println((!customerId.equals("0")));
						System.out.println((!customerId.equals("")));
						
						if(((!cartId.equals("0") && !cartId.equals("")))){
							System.out.println("entered into cart id is present");
							if(cartDao.isProductIdValid(productId) > 0 && cartDao.isCartIdValid(cartId) > 0){
							/*
							 * below folding list for checking whether given product id is there before in the cart or not.
							 */
							FoldingList<CartProductDetailsWrapper> cartProductDetailsFoldingList = cartDao.getCartProductDetails(cartId,productId);
							
							Map<String,Object>cartResultMap = new HashMap<String, Object>();
							if(cartProductDetailsFoldingList.getValues().size() > 0){
								cartResultMap = updateProductQuantityInCart(cartId,productId,productAttributeId);
								return new Reply(200,"SuccessFully Added",cartResultMap);
							} else {
								cartResultMap = addProductToCart(cartId,productId,productAttributeId);
								return new Reply(200,"SuccessFully Added",cartResultMap);
							}
						} else {
							errorLog.warn("productId or cartId is not valid. Give valid productId and cartId");
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "productId or cartId is not valid. Give valid productId and cartId");
							return invalidRequestReply;
						}
					} else if((cartId.equals(null) || cartId.equals(""))){
						cartId = cartDao.createCart(langId,currencyId,carrierId,customerId,guestId,secureKey).toString();
						Map<String,Object>cartResultMap = new HashMap<String, Object>();
						if(!cartId.equals("0")){
							cartResultMap = addProductToCart(cartId,productId,productAttributeId);
							return new Reply(200,"SuccessFully Added",cartResultMap);
						} else {
							return new Reply(200,"SuccessFully Added","");
						}
					} else {
						return null;
					}						
				} else {
					errorLog.warn("Some of the required fields are missing, specify necessary fields");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Some of the required fields are missing, specify necessary fields");
					return invalidRequestReply;
				}
				
			} else {
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
		} else {
			errorLog.warn("The parameter which you specified is not in json format");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "The parameter which you specified is not in json format");
			return invalidRequestReply;
		}	
	}
			catch (NullPointerException nullPointer) {
				System.out.println("in nullPointer");
				nullPointer.printStackTrace();
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
//			catch (ClassCastException classCast) {
//				errorLog.warn("Give accessToken as String,customerid as integer,requiredFields as array of strings");
//				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Give accessToken as String,customerid as integer,requiredFields as array of strings");
//				return invalidRequestReply;
//			}
			catch (ParseException parse) {
				System.out.println("in parse");
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
//			catch(StringIndexOutOfBoundsException indexOutOfBounds){
//				errorLog.warn("Specify your requirement in requiredFields array correctly");
//				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify your requirement in requiredFields array correctly");
//				return invalidRequestReply;
//			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					System.out.println("in illegal argument");
					e.printStackTrace();
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				}else {
					System.out.println("in internal server");
					e.printStackTrace();
					errorLog.warn("Internal server error");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
					return invalidRequestReply;
				}
			}
	}

	/**
	 * @param cartId
	 * @param productId
	 * @param productAttributeId
	 * @param productDetailsList
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> updateProductQuantityInCart(String cartId,String productId, String productAttributeId) {
		
		Map<String,Object>cartResultMap = new HashMap<String, Object>();
		List<Object>productDetailsList = new ArrayList<Object>();
		int totalCartQuantity = 0,updateProductQuantityInCart = 0,productQuantity = 0;
		double totalCartPrice = 0.0;
		updateProductQuantityInCart = cartDao.updateProductQuantityInCart(cartId,productId);
			totalCartPrice = cartHelper.getCartTotalPrice(cartId,productQuantity);
			productDetailsList = cartHelper.getProductDetails(productId);
			totalCartQuantity = cartHelper.getTotalCartQuantity(cartId);
			productQuantity = cartDao.getProductQuantity(cartId,productId);
			cartResultMap = cartHelper.addResultValuesToMap(cartId,cartDao.getCartQuantity(cartId),totalCartPrice,totalCartQuantity,productId,productDetailsList.get(0),productQuantity);
			return cartResultMap;
	}

	/**
	 * @param cartId
	 * @param productId
	 * @param productAttributeId
	 * @param productDetailsList 
	 * @return 
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> addProductToCart(String cartId, String productId,String productAttributeId) {
		
		Map<String,Object>cartResultMap = new HashMap<String, Object>();
		List<Object>productDetailsList = new ArrayList<Object>();
		int isProductAddedToCart = 0,totalCartQuantity = 0,productQuantity = 0;
		double totalCartPrice = 0.0;
		isProductAddedToCart = cartDao.addProductToExistingCart(cartId, productId, productAttributeId);
			productQuantity = cartDao.getProductQuantity(cartId,productId);
			totalCartQuantity = cartHelper.getTotalCartQuantity(cartId);
			totalCartPrice = cartHelper.getCartTotalPrice(cartId,productQuantity);
			productDetailsList = cartHelper.getProductDetails(productId);
			cartResultMap = cartHelper.addResultValuesToMap(cartId,cartDao.getCartQuantity(cartId),totalCartPrice,totalCartQuantity,productId,productDetailsList.get(0),productQuantity);
			return cartResultMap;
		}
	}
