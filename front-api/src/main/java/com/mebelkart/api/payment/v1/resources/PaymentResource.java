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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.mebelkart.api.payment.v1.dao.PaymentDao;
import com.mebelkart.api.util.classes.InvalidInputReplyClass;
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
		
		return null;
	}
}
