/**
 * 
 */
package com.mebelkart.api.mobile.v1.resources;

import java.util.ArrayList;
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
import com.mebelkart.api.mobile.v1.core.MobileDealsWrapper;
import com.mebelkart.api.mobile.v1.core.ProductDetailsWrapper;
import com.mebelkart.api.mobile.v1.dao.MobileDao;
import com.mebelkart.api.other.v1.core.DealsWrapper;
import com.mebelkart.api.other.v1.dao.OtherApiDao;
import com.mebelkart.api.other.v1.resources.OtherApiResource;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
@Path("/v1.0/mobile/")
@Produces({MediaType.APPLICATION_JSON})
public class MobileResource {
	
	MobileDao mobileDao;
	OtherApiDao otherDao;
	OtherApiResource otherResource;
	/**
	 * Default Constructer
	 */
	public MobileResource(MobileDao mobileDao,OtherApiDao otherDao){
		this.mobileDao = mobileDao;
		this.otherDao = otherDao;
	}
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(MobileResource.class);
	
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
	 * This will return json of best deals
	 * @return will return json of best deals
	 */
	@GET
	@Path("deals")
	@Timed
	public Object getDeals(@HeaderParam("accessParam") String accessParam,@QueryParam("cusId") int customerId,@QueryParam("cityId") int cityId,@QueryParam("nbr") int nbr,@QueryParam("refresh") String refresh){
		try{
			if(helper.isValidJson(accessParam)){
				if(isHavingValidAccessParamKeys(accessParam)){
					JSONObject jsonData = helper.jsonParser(accessParam);
					String userName = (String) jsonData.get("userName");
					String accessToken = (String) jsonData.get("accessToken");
					try {
						authenticate.validate(userName,accessToken, "mobile", "get", "getDeals");
					} catch (Exception e) {
						log.info("Unautherized user "+userName+" tried to access getDeals function");
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.UNAUTHORIZED.getStatusCode(), Response.Status.UNAUTHORIZED.getReasonPhrase(), e.getMessage());
						return invalidRequestReply;
					}
					if(otherResource == null)
						otherResource = new OtherApiResource(this.otherDao);
					if(customerId > 0 && cityId > 0 && nbr > 0){
						if("yes".equalsIgnoreCase(refresh) || "no".equalsIgnoreCase(refresh))
							return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), mobileDeals(otherResource,customerId,cityId,refresh,nbr));
						else{
							invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid refresh param, i.e, yes or no");
							return invalidRequestReply;
						}
					}else{
						invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Please provide valid Customer Id and City Id");
						return invalidRequestReply;
					}
					
				}else{
					log.info("Invalid header keys provided to access getDeals function");
					invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Invalid keys provided");
					return invalidRequestReply;
				}
			}else{
				log.info("Invalid header json provided to access getDeals function");
				invalidRequestReply = new InvalidInputReplyClass(Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase(), "Header data is invalid json/You may have not passed header details");
				return invalidRequestReply;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.warn("Internal error occured in getDeals function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}

	/**
	 * @param customerId
	 * @param cityId
	 * @param refresh
	 * @param nbr
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object mobileDeals(OtherApiResource otherResource,int customerId, int cityId, String refresh, int nbr) {
		List<MobileDealsWrapper> dealsWrapper = new ArrayList<MobileDealsWrapper>();
		// Here we are getting deals of the day data
		List<Object> bestDeals = (List<Object>)otherResource.bestdeals("mobile",nbr,refresh);
		// This part of code is to wrap the deals of the day data into mobile compatable
		dealsWrapper = wrap(dealsWrapper,bestDeals);
		// Here we are getting deals page data 
		List<Object> dealsPage = (List<Object>)otherResource.deals(customerId,cityId,refresh,nbr);
		// This part of code is to wrap the deals of the day data into mobile compatable
		dealsWrapper = wrap(dealsWrapper,dealsPage);
		return dealsWrapper;
	}

	/**
	 * @param dealsWrapper
	 * @param bestDeals
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<MobileDealsWrapper> wrap(List<MobileDealsWrapper> dealsWrapper, List<Object> dealsPage) {
		// This part of code is to wrap the deals of the day data into mobile compatable
		for(int i = 0; i < dealsPage.size(); i++){
			Map<String,Object> dealsWrap = (Map<String,Object>) dealsPage.get(i);
			MobileDealsWrapper wrapper = new MobileDealsWrapper();
			wrapper.setType("deals");
			wrapper.setCategory_id((Integer)dealsWrap.get("categoryId")+"");
			wrapper.setCategory_name((String) dealsWrap.get("catName"));
			List<DealsWrapper> proWrap = (List<DealsWrapper>) dealsWrap.get("products");
			List<ProductDetailsWrapper> innerProdWrapper = new ArrayList<ProductDetailsWrapper>();
			for(int j = 0; j < proWrap.size(); j++){
				ProductDetailsWrapper wrap = new ProductDetailsWrapper();
				wrap.setType("product");
				wrap.setProduct_id(proWrap.get(j).getProductId()+"");
				wrap.setCategory_id(proWrap.get(j).getCatId()+"");
				wrap.setBrand_id(0);
				wrap.setImag_url(proWrap.get(j).getProductImage());
				wrap.setTitle(proWrap.get(j).getProductName());
				wrap.setOffer_text("");
				wrap.setIs_sold_out(0);
				wrap.setMkt_price(proWrap.get(j).getMktPrice());
				wrap.setOur_price(proWrap.get(j).getOurPrice());
				wrap.setFlash_sale_date_end(proWrap.get(j).getFlashSaleEndDate());
				innerProdWrapper.add(wrap);
			}
			wrapper.setChildren(innerProdWrapper);
			dealsWrapper.add(wrapper);
		}
		return dealsWrapper;
	}

	private boolean isHavingValidAccessParamKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken")){
			return true;
		}else
			return false;
	}
}
