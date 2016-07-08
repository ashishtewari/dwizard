/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.ideaboard.v1.core.NBProductsWrapper;
import com.mebelkart.api.ideaboard.v1.core.WishListProductsWrapper;
import com.mebelkart.api.ideaboard.v1.core.WishListWrapper;
import com.mebelkart.api.ideaboard.v1.dao.IdeaBoardDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
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
	
	@GET
	@Path("getideaboards")
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
	
	/**
	 * @param customerId
	 * @return
	 */
	private Object getWishLists(int customerId) {
		List<Object> wishLists = new ArrayList<Object>();
		Map<String,Object> wishList = new HashMap<String,Object>();
		List<WishListWrapper> wishListData = this.ideaBoardDao.getWishListByCustId(customerId);
		System.out.println(wishListData.size());
		List<NBProductsWrapper> nbProducts = this.ideaBoardDao.getInfosByIdCustomer(customerId);
		System.out.println(nbProducts.size());
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