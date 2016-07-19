/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.digest.DigestUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.mkApiConfiguration;
import com.mebelkart.api.ideaboard.v1.core.NBProductsWrapper;
import com.mebelkart.api.ideaboard.v1.core.WishListProductsWrapper;
import com.mebelkart.api.ideaboard.v1.core.WishListWrapper;
import com.mebelkart.api.ideaboard.v1.dao.IdeaBoardDao;
import com.mebelkart.api.product.v1.api.ProductDetailsResponse;
import com.mebelkart.api.product.v1.resources.ProductResource;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.factories.ElasticFactory;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
@Path("/v1.0")
@Produces({MediaType.APPLICATION_JSON})
public class IdeaBoardResource {
	
	IdeaBoardDao ideaBoardDao;
	
	/**
	 * Default Constructer
	 */
	public IdeaBoardResource(IdeaBoardDao ideaBoardDao) {
		this.ideaBoardDao = ideaBoardDao;
	}	
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(IdeaBoardResource.class);
	
	/**
	 * InvalidInputReplyClass class
	 */
	InvalidInputReplyClass invalidRequestReply = null;	
	/**
	 * Getting client to authenticate
	 */
	Authentication authenticate = new Authentication();

	/**
	 * Helper class from utils
	 */
	Helper helper = new Helper();
	
	/**
	 * Getting products elastic client connection
	 */
	Client client = ElasticFactory.getProductsElasticClient();
	
	@GET
	@Path("ideaboards")
	@Timed
	public Object getIdeaBoards(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "getIdeaBoards");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getIdeaBoards function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(customerId > 0){
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), getWishLists(customerId));
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customer ID");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access getIdeaBoards function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getIdeaBoards function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getIdeaBoards function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	@GET
	@Path("ideaboard/products")
	@Timed
	public Object getIdeaBoardsProducts(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,@QueryParam("wishListId") int wishListId,@QueryParam("pincode") int pincode){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "getIdeaBoardsProducts");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getIdeaBoardsProducts function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(customerId > 0 && wishListId > 0){
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), getProductsOfIdeaboard(wishListId,customerId));
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customer ID");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access getIdeaBoardsProducts function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getIdeaBoardsProducts function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getIdeaBoardsProducts function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	@POST
	@Path("ideaboard/new")
	@Timed
	public Object createNewIdeaBoards(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,@QueryParam("name") String ideaBoardName){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "createNewIdeaBoards");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access createNewIdeaBoards function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(customerId > 0 && (ideaBoardName != null && !ideaBoardName.equals("") )){
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), createNewIB(customerId,ideaBoardName));
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customer ID and wish list name");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access createNewIdeaBoards function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access createNewIdeaBoards function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in createNewIdeaBoards function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	@PUT
	@Path("ideaboard/update")
	@Timed
	public Object updateIdeaBoards(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,@QueryParam("name") String ideaBoardName,@QueryParam("wishlistId") int wishlistId){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "updateIdeaBoards");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access updateIdeaBoards function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(customerId > 0 && (ideaBoardName != null && !ideaBoardName.equals("")) && wishlistId > 0){
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), updateIB(customerId,wishlistId,ideaBoardName));
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customer ID and wishlist name and wishlist Id");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access updateIdeaBoards function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access updateIdeaBoards function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in updateIdeaBoards function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	@DELETE
	@Path("ideaboard/delete")
	@Timed
	public Object deleteIdeaBoard(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,@QueryParam("wishlistId") int wishlistId){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "deleteIdeaBoard");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access deleteIdeaBoard function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					
					if(customerId > 0 && wishlistId > 0){
						return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), deleteIB(customerId,wishlistId));
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customer ID and wishlist Id");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access deleteIdeaBoard function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access deleteIdeaBoard function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in deleteIdeaBoard function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("ideaboard/additem")
	@Timed
	public Object addItemToIdeaBoard(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,
			@QueryParam("wishlistId") int wishlistId,@QueryParam("prodId") int prodId,@QueryParam("prodAttrId") int prodAttrId,
			@QueryParam("quantity") int quantity){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "ideaboard", "get", "addItemToIdeaBoard");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access addItemToIdeaBoard function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}					
					if(customerId > 0 && wishlistId > 0 && prodId > 0 && prodAttrId >= 0 && quantity > 0){
						String response = ((Map<String,String>)addItemToIB(wishlistId,customerId,prodId,prodAttrId,quantity)).get("msg");
						if(response.equals("Added Successfully"))
							return new Reply(Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase(), response);
						else
							return new Reply(Response.Status.NOT_MODIFIED.getStatusCode(), Response.Status.NOT_MODIFIED.getReasonPhrase(), response);
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid customerID, wishlistId, productId, productAttributeId and quantity");
						return invalidRequestReply;
					}
				}else{
					log.info("Invalid header keys provided to access addItemToIdeaBoard function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access addItemToIdeaBoard function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in addItemToIdeaBoard function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * @param wishlistId
	 * @param customerId
	 * @param prodId
	 * @param prodAttrId
	 * @param quantity
	 * @return
	 */
	private Object addItemToIB(int wishlistId, int customerId, int prodId,int prodAttrId, int quantity) {
		Map<String,String> response = new HashMap<String,String>();
		List<Integer> result = this.ideaBoardDao.checkIfItemExists(wishlistId,customerId,prodId,prodAttrId);
		if(!result.isEmpty() && result.size() > 0){
			response.put("msg", "You already have this item in your WishBoard");
			return response;
		}else if(this.ideaBoardDao.addWishBoard(wishlistId,prodId,prodAttrId,quantity) > 0){
			response.put("msg", "Added Successfully");
			return response;
		}else{
			response.put("msg", "WishBoard not added");
			return response;
		}
	}

	/**
	 * @param customerId
	 * @param ideaBoardName
	 * @return
	 */
	private Object createNewIB(int customerId, String ideaBoardName) {
		List<String> errors = new ArrayList<String>();
		if(this.ideaBoardDao.isExistsByNameForUser(customerId,ideaBoardName) > 0)
			errors.add("This name is already used by another list.");
		if(errors.size() == 0){
			String uniqId = (UUID.randomUUID().toString())+mkApiConfiguration.getCookieKey()+customerId;
			String sha1 = DigestUtils.sha1Hex(uniqId);
			String token = sha1.substring(0, 16).toUpperCase();
			String counter = null;
			this.ideaBoardDao.createNewIB(customerId,token,ideaBoardName,counter,helper.getCurrentDateString(),helper.getCurrentDateString());
		}
		List<Object> wishLists = new ArrayList<Object>();
		Map<String,Object> wishList = new HashMap<String,Object>();
		List<WishListWrapper> wishListData = this.ideaBoardDao.getWishListByCustId(customerId);
		List<NBProductsWrapper> nbProducts = this.ideaBoardDao.getInfosByIdCustomer(customerId);
		for(int i = 0; i < wishListData.size(); i++){
			for(int j = 0; j < nbProducts.size(); j++){
				if(nbProducts.get(j).getWishListId() == wishListData.get(i).getWishListId()){
					wishListData.get(i).setTotalProducts(nbProducts.get(j).getNbProducts());
				}else{
					wishListData.get(i).setTotalProducts(0);
				}
			}
			wishLists.add(wishListData.get(i));
		}
		wishList.put("wishLists", wishLists);
		wishList.put("customerId", customerId);
		wishList.put("errors", errors.size() > 0 ? errors.size() : "");
		if(errors.size() == 0)
			wishList.put("msg", "Added Successfully");
		else
			wishList.put("msg", errors.get(0));
		return wishList;
	}
	
	/**
	 * @param customerId
	 * @param wishlistId
	 * @param ideaBoardName
	 * @return
	 */
	private Object updateIB(int customerId, int wishlistId, String ideaBoardName) {
		List<String> errors = new ArrayList<String>();
		if(this.ideaBoardDao.isExistsByNameForUser(customerId,ideaBoardName) > 0)
			errors.add("This name is already used by another list.");
		if(errors.size() == 0){
			String uniqId = (UUID.randomUUID().toString())+mkApiConfiguration.getCookieKey()+customerId;
			String sha1 = DigestUtils.sha1Hex(uniqId);
			String token = sha1.substring(0, 16).toUpperCase();
			String counter = null;
			this.ideaBoardDao.updateIB(wishlistId,customerId,token,ideaBoardName,counter,helper.getCurrentDateString());
		}
		List<Object> wishLists = new ArrayList<Object>();
		Map<String,Object> wishList = new HashMap<String,Object>();
		List<WishListWrapper> wishListData = this.ideaBoardDao.getWishListByCustId(customerId);
		List<NBProductsWrapper> nbProducts = this.ideaBoardDao.getInfosByIdCustomer(customerId);
		for(int i = 0; i < wishListData.size(); i++){
			for(int j = 0; j < nbProducts.size(); j++){
				if(nbProducts.get(j).getWishListId() == wishListData.get(i).getWishListId()){
					wishListData.get(i).setTotalProducts(nbProducts.get(j).getNbProducts());
				}else{
					wishListData.get(i).setTotalProducts(0);
				}
			}
			wishLists.add(wishListData.get(i));
		}
		wishList.put("wishLists", wishLists);
		wishList.put("customerId", customerId);
		wishList.put("errors", errors.size() > 0 ? errors.size() : "");
		if(errors.size() == 0 && wishLists.size() > 0)
			wishList.put("msg", "Updated Successfully");
		else if(errors.size() > 0)
			wishList.put("msg", errors.get(0));
		else
			wishList.put("msg", "WishBoard not updated");
		return wishList;
	}
	
	/**
	 * @param customerId
	 * @param wishlistId
	 * @return
	 */
	private Object deleteIB(int customerId, int wishlistId) {
		Map<String,Object> wishList = new HashMap<String,Object>();
		if(this.ideaBoardDao.isValidWishlistIdWithRespectiveToCustId(customerId,wishlistId) > 0){
			this.ideaBoardDao.deleteFromWishlist(wishlistId);
			this.ideaBoardDao.deleteFromWishlistEmail(wishlistId);
			this.ideaBoardDao.deleteFromWishlistProduct(wishlistId);
			return wishList.put("msg", "Deleted Successfully");
		}else
			return wishList.put("msg", "Cannot delete this WishBoard");
	}
	
	/**
	 * @param wishListId
	 * @param customerId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getProductsOfIdeaboard(int wishListId, int customerId) {
		List<ProductDetailsResponse> product = new ArrayList<ProductDetailsResponse>();
		ProductResource prod = new ProductResource();
		//List<WishListProductsWrapper> products = getWishlitProduct(wishListId,customerId);
 		List<WishListProductsWrapper> products = this.ideaBoardDao.getProductsByIdCustomer(wishListId,customerId);
 		for(int i = 0; i < products.size(); i++){
 			GetResponse response = client.prepareGet("mkproducts", "product", products.get(i).getProductId()+"").get();
			ProductDetailsResponse prodDetails = new ProductDetailsResponse();
			if(response.isExists()){
				Map<String,Object> source = response.getSource();
				Map<String,Object> info = (Map<String, Object>) source.get("info");
				Map<String,Object> categoryVars = (Map<String, Object>) source.get("categoryVars");
				
				prodDetails = new ProductDetailsResponse();
				prodDetails.setProductId((String)info.get("id_product"));
				prodDetails.setCategoryId((String)info.get("id_category_default"));
				prodDetails.setCategoryName((String)info.get("name_category_default"));
				prodDetails.setProductName((String)info.get("name"));
				prodDetails.setProductDesc((String)info.get("description"));
				prodDetails.setAvailLocation((String) source.get("all_locations"));
				prodDetails.setBrandId(null);
				prodDetails.setBrandName(null);
				prodDetails.setTotalViews((String)source.get("views_count"));
				prodDetails.setShippingCost((String)info.get("additional_shipping_cost"));
				prodDetails.setShippingAvailable((String)info.get("available_now"));
				prodDetails.setGallery(prod.getGallery(source));
				prodDetails.setOfferText(null);
				prodDetails.setMktPrice((Integer)categoryVars.get("price_without_reduction"));
				prodDetails.setOurPrice((Integer)categoryVars.get("price_tax_exc"));
				prodDetails.setEmiPrice("https://www.mebelkart.com/getEMIForProduct/"+(String)info.get("id_product")+"?mode=json");
				prodDetails.setRating((Integer) source.get("product_rating"));
				prodDetails.setAttributes((String) categoryVars.get("id_product_attribute"));
				prodDetails.setProductFeatures(prod.getProductFeatures(source));
				prodDetails.setIsSoldOut(0);
				prodDetails.setAttributeGroups(prod.setAttributeGroups(source));
				prodDetails.setReviews(null);
				prodDetails.setTotalReviews(0);
				prodDetails.setTotalReviewsCount(0);
				product.add(prodDetails);
			}
		}		
		return product;
	}

	/**
	 * @param customerId
	 * @return
	 */
	private Object getWishLists(int customerId) {
		List<Object> wishLists = new ArrayList<Object>();
		Map<String,Object> wishList = new HashMap<String,Object>();
		List<WishListWrapper> wishListData = this.ideaBoardDao.getWishListByCustId(customerId);
		List<NBProductsWrapper> nbProducts = this.ideaBoardDao.getInfosByIdCustomer(customerId);
		for(int i = 0; i < wishListData.size(); i++){
			for(int j = 0; j < nbProducts.size(); j++){
				if(nbProducts.get(j).getWishListId() == wishListData.get(i).getWishListId()){
					wishListData.get(i).setTotalProducts(((List<WishListProductsWrapper>)(this.ideaBoardDao.getProductsByIdCustomer(wishListData.get(i).getWishListId(),customerId))).size());
					wishListData.get(i).setTotalNoProducts(nbProducts.get(j).getNbProducts());
				}
			}
			if(wishListData.get(i).getTotalProducts() < 0)
				wishListData.get(i).setTotalProducts(0);
			if(wishListData.get(i).getTotalNoProducts() < 0)
				wishListData.get(i).setTotalNoProducts(0);
			wishLists.add(wishListData.get(i));
		}
		wishList.put("wishLists", wishLists);
		return wishList;
	}

	private boolean isHavingValidAccessParamKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken")){
			return true;
		}else
			return false;
	}

}
