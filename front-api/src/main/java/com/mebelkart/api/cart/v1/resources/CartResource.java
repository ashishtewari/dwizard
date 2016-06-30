/**
 * 
 */
package com.mebelkart.api.cart.v1.resources;


import java.util.HashMap;
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
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.mkApiApplication;
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
					String accessToken = headerInputJsonData.get("accessToken").toString();
					String userName = headerInputJsonData.get("userName").toString();
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
					
					if(bodyInputJsonData.containsKey("id_product") && bodyInputJsonData.containsKey("id_product_attribute") && bodyInputJsonData.containsKey("id_customer") && bodyInputJsonData.containsKey("secure_key") && bodyInputJsonData.containsKey("id_cart") && bodyInputJsonData.containsKey("operator")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String productId = bodyInputJsonData.get("id_product").toString();
						String productAttributeId = bodyInputJsonData.get("id_product_attribute").toString();
						String customerId = bodyInputJsonData.get("id_customer").toString();
						String secureKey = bodyInputJsonData.get("secure_key").toString();
						String operator = bodyInputJsonData.get("operator").toString();
						String guestId = "";
						if(bodyInputJsonData.containsKey("id_guest")){
							guestId = bodyInputJsonData.get("id_guest").toString();
						} else {
							guestId = "0";
						}
						if(customerId.equals("") || customerId.equals("0")){
							customerId = "0";
						}
						
						int isProductAddedToCart = 0,totalCartQuantity = 0,increaseProductQuantityInCart = 0,productQuantity = 0,isCartCreated = 0;
						int langId = 1, currencyId = 4,carrierId = 0;
						double totalCartPrice = 0.0;
						
						if(!cartId.equals("0") && !cartId.equals("") && !cartId.equals(null)){
							if(cartDao.isProductIdValid(productId) > 0 && cartDao.isCartIdValid(cartId) > 0){
								int isCartHavingThisProductBefore = cartDao.getCartProductDetails(cartId,productId);
								Map<String,Object>cartResultMap = new HashMap<String, Object>();
								if(isCartHavingThisProductBefore > 0){
									cartResultMap = increaseProductQuantityInCart(cartId,productId,productAttributeId,operator);
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
							
						} else {
							//if((cartId.equals(null) || cartId.equals("") || cartId.equals("0")))
							Map<String,Object>cartResultMap = new HashMap<String, Object>();
							cartId = cartDao.createCart(langId,currencyId,carrierId,customerId,guestId,secureKey).toString();
							
							if(!cartId.equals("0")){
								cartResultMap = addProductToCart(cartId,productId,productAttributeId);
								return new Reply(200,"SuccessFully Added",cartResultMap);
								
							} else {
								return new Reply(200,"Not Added","");
							}
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
			catch (UnableToExecuteStatementException mysqlException) {
				mysqlException.printStackTrace();
				errorLog.warn("Specify all required values for fields in request");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify all required values for fields in request");
				return invalidRequestReply;
			}
			catch (ClassCastException classCast) {
				errorLog.warn("Give accessToken as String,customerid as integer,requiredFields as array of strings");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Give accessToken as String,customerid as integer,requiredFields as array of strings");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				System.out.println("in parse");
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
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
	
	
	@POST
	@Path("/decreaseProduct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Timed
	public Object decreaseProductFromCart(@HeaderParam("accessParam")String accessParam,@Context HttpServletRequest request){
		try {
			cartHelper = new CartHelper(cartDao);	
			if(utilHelper.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
				bodyInputJsonData = utilHelper.contextRequestParser(request);
				if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
					String accessToken = headerInputJsonData.get("accessToken").toString();
					String userName = headerInputJsonData.get("userName").toString();
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
					
					if(bodyInputJsonData.containsKey("id_product") && bodyInputJsonData.containsKey("id_product_attribute") && bodyInputJsonData.containsKey("id_customer") && bodyInputJsonData.containsKey("secure_key") && bodyInputJsonData.containsKey("id_cart") && bodyInputJsonData.containsKey("operator")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String productId = bodyInputJsonData.get("id_product").toString();
						String productAttributeId = bodyInputJsonData.get("id_product_attribute").toString();
						String operator = bodyInputJsonData.get("operator").toString();
						
						if(((!cartId.equals("0") && !cartId.equals("") && !cartId.equals(null)) || (!productId.equals("0") && !productId.equals("") && !productId.equals(null)))){
							if(cartDao.isProductIdValid(productId) > 0 && cartDao.isCartIdValid(cartId) > 0){
								int isCartHavingThisProductBefore = cartDao.getCartProductDetails(cartId,productId);
								Map<String,Object>cartResultMap = new HashMap<String, Object>();
									if(isCartHavingThisProductBefore > 0){
										cartResultMap = increaseProductQuantityInCart(cartId,productId,productAttributeId,operator);
										return new Reply(200,"SuccessFully Added",cartResultMap);
									} else {
										errorLog.warn("This product is not in the given cart. Give valid productId and cartId");
										invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "This product is not in the given cart. Give valid productId and cartId");
										return invalidRequestReply;
									}
								} else {
									errorLog.warn("productId or cartId is not valid. Give valid productId and cartId");
									invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "productId or cartId is not valid. Give valid productId and cartId");
									return invalidRequestReply;
								}
							
							} else {
								errorLog.warn("Specify cartId and productId to reduce the product in cart");
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify cartId and productId to reduce the product in cart");
								return invalidRequestReply;
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
				nullPointer.printStackTrace();
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
			catch (UnableToExecuteStatementException mysqlException) {
				mysqlException.printStackTrace();
				errorLog.warn("Specify all required values for fields in request");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify all required values for fields in request");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					e.printStackTrace();
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				}else {
					e.printStackTrace();
					errorLog.warn("Internal server error");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
					return invalidRequestReply;
				}
			}
		
	}
	
	
	@SuppressWarnings("unused")
	@POST
	@Path("/deleteProduct")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Timed
	public Object deleteProductFromCart(@HeaderParam("accessParam")String accessParam,@Context HttpServletRequest request){
		try {
			cartHelper = new CartHelper(cartDao);	
			if(utilHelper.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
				bodyInputJsonData = utilHelper.contextRequestParser(request);
				if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
					String accessToken = headerInputJsonData.get("accessToken").toString();
					String userName = headerInputJsonData.get("userName").toString();
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
					
					if(bodyInputJsonData.containsKey("id_product") && bodyInputJsonData.containsKey("id_product_attribute") && bodyInputJsonData.containsKey("id_cart") && bodyInputJsonData.containsKey("operator")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String productId = bodyInputJsonData.get("id_product").toString();
						String productAttributeId = bodyInputJsonData.get("id_product_attribute").toString();
						String operator = bodyInputJsonData.get("operator").toString();
						int isProductDeletedFromCart = 0,productQuantity = 0;
						
						if(((!cartId.equals("0") && !cartId.equals("") && !cartId.equals(null)) || (!productId.equals("0") && !productId.equals("") && !productId.equals(null)))){
							if(cartDao.isProductIdValid(productId) > 0 && cartDao.isCartIdValid(cartId) > 0){
								int isCartHavingThisProductBefore = cartDao.getCartProductDetails(cartId,productId);
								Map<String,Object>cartResultMap = new HashMap<String, Object>();
									if(isCartHavingThisProductBefore > 0){
										isProductDeletedFromCart = cartDao.deleteProductFromCart(cartId,productId);
										cartResultMap.put("cart_id", cartId);
										cartResultMap.put("cart_quantity", 0);
										cartResultMap.put("total_cart_quantity", cartDao.getCartTotalQuantity(cartId));
										cartResultMap.put("total_cart_price", cartHelper.getCartTotalPrice(cartId));
										return new Reply(200,"SuccessFully Deleted",cartResultMap);
									} else {
										errorLog.warn("This product is not in the given cart. Give valid productId and cartId");
										invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "This product is not in the given cart. Give valid productId and cartId");
										return invalidRequestReply;
									}
								} else {
									errorLog.warn("productId or cartId is not valid. Give valid productId and cartId");
									invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "productId or cartId is not valid. Give valid productId and cartId");
									return invalidRequestReply;
								}
							
							} else {
								errorLog.warn("Specify cartId and productId to reduce the product in cart");
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify cartId and productId to reduce the product in cart");
								return invalidRequestReply;
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
				nullPointer.printStackTrace();
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
			catch (UnableToExecuteStatementException mysqlException) {
				mysqlException.printStackTrace();
				errorLog.warn("Specify all required values for fields in request");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify all required values for fields in request");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					e.printStackTrace();
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				}else {
					e.printStackTrace();
					errorLog.warn("Internal server error");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
					return invalidRequestReply;
				}
			}
		
	}
	
	
	
	@POST
	@Path("/cartSummary")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Timed
	public Object getCartSummary(@HeaderParam("accessParam")String accessParam,@Context HttpServletRequest request){
		try {
			cartHelper = new CartHelper(cartDao);	
			if(utilHelper.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
				bodyInputJsonData = utilHelper.contextRequestParser(request);
				if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
					String accessToken = headerInputJsonData.get("accessToken").toString();
					String userName = headerInputJsonData.get("userName").toString();
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
					
					if( bodyInputJsonData.containsKey("id_cart")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String addressId = "0";
						if(bodyInputJsonData.containsKey("id_address") && !(Integer.parseInt(bodyInputJsonData.get("id_address").toString()) > 0) && !bodyInputJsonData.get("id_address").equals("0") && !bodyInputJsonData.get("id_address").equals("") && !bodyInputJsonData.get("id_address").equals(null)){
							addressId = bodyInputJsonData.get("id_address").toString();
						}
						
						if(((!cartId.equals("0") && !cartId.equals("") && !cartId.equals(null)))){
							if(cartDao.isCartIdValid(cartId) > 0 && cartDao.isProductsPresentCart(cartId) > 0){	
								Map<String,Object>cartResultMap = new HashMap<String, Object>();
										cartResultMap = cartHelper.getProductDetailsInCart(cartId,addressId);
										return new Reply(200,"Success",cartResultMap);
								} else {
									errorLog.warn("cartId is not valid. Give valid cartId");
									invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "cartId is not valid. Give valid cartId");
									return invalidRequestReply;
								}
							
							} else {
								errorLog.warn("Specify cartId to get cart summary");
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify cartId to get cart summary");
								return invalidRequestReply;
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
				nullPointer.printStackTrace();
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
			catch (UnableToExecuteStatementException mysqlException) {
				mysqlException.printStackTrace();
				errorLog.warn("Specify all required values for fields in request");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify all required values for fields in request");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					e.printStackTrace();
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				}else {
					e.printStackTrace();
					errorLog.warn("Internal server error");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Internal server error");
					return invalidRequestReply;
				}
			}
		
	}

	
	
	@SuppressWarnings("unused")
	@POST
	@Path("/updateCartAddress")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Timed
	public Object updateCartAddress(@HeaderParam("accessParam")String accessParam,@Context HttpServletRequest request){
		try {
			cartHelper = new CartHelper(cartDao);	
			if(utilHelper.isValidJson(accessParam)){ // validating the input json data
				headerInputJsonData = (JSONObject) parser.parse(accessParam); // parsing header parameter values
				bodyInputJsonData = utilHelper.contextRequestParser(request);
				if(headerInputJsonData.containsKey("accessToken") && headerInputJsonData.containsKey("userName")) {
					String accessToken =  headerInputJsonData.get("accessToken").toString();
					String userName =  headerInputJsonData.get("userName").toString();
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
					
					if( bodyInputJsonData.containsKey("id_cart") && bodyInputJsonData.containsKey("id_address_delivery")){
						String cartId = bodyInputJsonData.get("id_cart").toString();
						String addressId = bodyInputJsonData.get("id_address_delivery").toString();
						int isCartAddressUpdated = 0,productQuantity = 0;
//						if(bodyInputJsonData.containsKey("id_address") && !(Integer.parseInt(bodyInputJsonData.get("id_address").toString()) > 0) && !bodyInputJsonData.get("id_address").equals("0") && !bodyInputJsonData.get("id_address").equals("") && !bodyInputJsonData.get("id_address").equals(null)){
//							addressId = bodyInputJsonData.get("id_address").toString();
//						}
						
						if((!cartId.equals("0") && !cartId.equals("") && !cartId.equals(null)) && (!addressId.equals("0") && !addressId.equals("") && !addressId.equals(null))){
							if(cartDao.isCartIdValid(cartId) > 0 && cartDao.isAddressIdValid(addressId) > 0){

								Map<String,Object>cartResultMap = new HashMap<String, Object>();
										//cartResultMap = cartHelper.getProductDetailsInCart(cartId,addressId);
								isCartAddressUpdated = cartDao.updateCartAddress(cartId,addressId);
								cartResultMap.put("cart_id", cartId);
								cartResultMap.put("cart_quantity", cartDao.getCartQuantity(cartId).toString());
								cartResultMap.put("total_cart_quantity", cartDao.getCartTotalQuantity(cartId));
								cartResultMap.put("total_cart_price", cartHelper.getCartTotalPrice(cartId));
										return new Reply(200,"Success",cartResultMap);
								} else {
									errorLog.warn("cartId or addressId is not valid. Give valid values");
									invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "cartId or addressId is not valid. Give valid values");
									return invalidRequestReply;
								}
							
							} else {
								errorLog.warn("Specify cartId to get cart summary");
								invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify cartId to get cart summary");
								return invalidRequestReply;
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
				nullPointer.printStackTrace();
				errorLog.warn("accessToken or userName spelled Incorrectly or mention necessary fields");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "accessToken or userName spelled Incorrectly or mention necessary fields");
				return invalidRequestReply;
			}
			catch (UnableToExecuteStatementException mysqlException) {
				mysqlException.printStackTrace();
				errorLog.warn("Specify all required values for fields in request");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify all required values for fields in request");
				return invalidRequestReply;
			}
			catch (ParseException parse) {
				parse.printStackTrace();
				errorLog.warn("Specify correct data type for the values as mentioned in instructions");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct data type for the values as mentioned in instructions");
				return invalidRequestReply;
			}
			catch (Exception e) {
				if(e instanceof IllegalArgumentException){
					e.printStackTrace();
					errorLog.warn("Specify correct keys for the values as mentioned in instructions");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Specify correct keys for the values as mentioned in instructions");
					return invalidRequestReply;
				}else {
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
	 * @return 
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> addProductToCart(String cartId, String productId,String productAttributeId) {
		
		Map<String,Object>cartResultMap = new HashMap<String, Object>();
		String productName = "";
		int isProductAddedToCart = 0,totalCartQuantity = 0,productQuantity = 0;
		double totalCartPrice = 0.0;
		isProductAddedToCart = cartDao.addProductToExistingCart(cartId, productId, productAttributeId);
			productQuantity = cartDao.getProductQuantity(cartId,productId);
			totalCartQuantity = cartDao.getCartTotalQuantity(cartId);
			totalCartPrice = cartHelper.getCartTotalPrice(cartId);
			productName = cartHelper.getProductName(productId);
			cartResultMap = cartHelper.addResultValuesToMap(cartId,cartDao.getCartQuantity(cartId),totalCartPrice,totalCartQuantity,productId,productName,productQuantity);
			return cartResultMap;
		}
	
	
	
	/**
	 * @param cartId
	 * @param productId
	 * @param productAttributeId
	 * @param operator 
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<String, Object> increaseProductQuantityInCart(String cartId,String productId, String productAttributeId, String operator) {
		
		Map<String,Object>cartResultMap = new HashMap<String, Object>();
		int totalCartQuantity = 0,increaseProductQuantityInCart = 0,productQuantity = 0;
		double totalCartPrice = 0.0;
		String productName = "";
		if(operator.equals("up")) {
			increaseProductQuantityInCart = cartDao.increaseProductQuantityInCart(cartId,productId);
		} else if(operator.equals("down")) {
			increaseProductQuantityInCart = cartDao.reduceProductQuantityInCart(cartId,productId);
		}
			totalCartPrice = cartHelper.getCartTotalPrice(cartId);
			productName = cartHelper.getProductName(productId);
			totalCartQuantity = cartDao.getCartTotalQuantity(cartId);
			productQuantity = cartDao.getProductQuantity(cartId,productId);
			cartResultMap = cartHelper.addResultValuesToMap(cartId,cartDao.getCartQuantity(cartId),totalCartPrice,totalCartQuantity,productId,productName,productQuantity);
			return cartResultMap;
	}
	}
