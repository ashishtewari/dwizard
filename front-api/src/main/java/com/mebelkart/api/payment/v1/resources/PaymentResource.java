/**
 * 
 */
package com.mebelkart.api.payment.v1.resources;

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
import com.mebelkart.api.payment.v1.dao.PaymentDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
import com.mebelkart.api.util.classes.Reply;
import com.mebelkart.api.util.helpers.Authentication;
import com.mebelkart.api.util.helpers.Helper;

/**
 * @author Tinku
 *
 */
@Path("/v1.0/payment/")
@Produces({MediaType.APPLICATION_JSON})
public class PaymentResource {
	
	PaymentDao paymentDao;
	/**
	 * Default Constructer
	 */
	public PaymentResource(PaymentDao dao){
		this.paymentDao = dao;
	}
	/**
	 * Get actual class name to be printed on log files
	 */
	static Logger log = LoggerFactory.getLogger(PaymentResource.class);
	
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
	@Path("methods")
	@Timed
	public Object getPaymentMethods(@HeaderParam("accessParam") String accessParam,@QueryParam("cartId") int cartId){
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
					
					if(cartId > 0){
						return new Reply(Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase(), getPaymentMethodDetails(cartId));
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
			log.warn("Internal error occured in getPaymentMethods function");
			invalidRequestReply = new InvalidInputReplyClass(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase(), "Unknown exception caused");
			return invalidRequestReply;
		}
	}
	
	/**
	 * @param cartId
	 * @return
	 */
	@SuppressWarnings("unused")
	private Object getPaymentMethodDetails(int cartId) {
		int address = this.paymentDao.getAddress(cartId);
		Object paymentModeRestrictionByCartProducts = fetchPaymentModeRestrictionsByCartProducts(cartId);
		Object shippingAndPaymentAvailabilityOnPincode = blockProceedingsAtOrderStepsIfNotShippable(cartId);
		return null;
	}

	/**
	 * @param cartId
	 * @return
	 */
	private Object blockProceedingsAtOrderStepsIfNotShippable(int cartId) {
		return null;
	}

	/**
	 * @param cartId
	 * @return
	 */
	private Object fetchPaymentModeRestrictionsByCartProducts(int cartId) {
		
		return null;
	}

	private boolean isHavingValidAccessParamKeys(String accessParam) {
		JSONObject jsonData = helper.jsonParser(accessParam);
		if(jsonData.containsKey("userName") && jsonData.containsKey("accessToken")){
			return true;
		}else
			return false;
	}
}
