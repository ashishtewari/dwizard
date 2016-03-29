/**
 * 
 */
package com.mebelkart.api.util.exceptions;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mebelkart.api.mkApiApplication;
import com.mebelkart.api.util.Reply;

/**
 * @author Nikky-Akky
 *
 */
public class HandleException {

	private int statusCode;
	private String message;
	HandleException exception = null;
	static Logger errorLog = LoggerFactory.getLogger(mkApiApplication.class);
	
	public HandleException() {
		this.statusCode = 0;
		this.message = null;
	} 

	public HandleException(int statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}
	
	/**
	 * @param customMessage
	 * @param source
	 * @return
	 */
	public Reply getException(String customMessage, Object source) {
		this.message = "HTTP " + this.statusCode + " " + this.message
				+ ", "+customMessage;		
		return new Reply(this.statusCode, this.message, source);
	}

	/**
	 * @param validateResponse
	 * @return exception for the specified input
	 */
	public Reply accessTokenException(int validateResponse) {
		// TODO Auto-generated method stub
		if(validateResponse == 0){
			exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
			errorLog.warn("Invalid API key specified");
			return exception.getException("Invalid API key specified",null);
		}else if(validateResponse == -1){
			exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
			errorLog.warn("API key specified was not active");
			return exception.getException("API key specified was not active",null);
		}
		else if(validateResponse == -2){
			exception = new HandleException(Response.Status.UNAUTHORIZED.getStatusCode(),Response.Status.UNAUTHORIZED.getReasonPhrase());
			errorLog.warn("You are not authorized to access this resource");
			return exception.getException("You are not authorized to access this resource",null);
		}
		else if(validateResponse == -3){
			exception = new HandleException(Response.Status.METHOD_NOT_ALLOWED.getStatusCode(),Response.Status.METHOD_NOT_ALLOWED.getReasonPhrase());
			errorLog.warn("You are not authorized to access this method");
			return exception.getException("You are not authorized to access this method",null);
		}
		else{
			exception = new HandleException(Response.Status.BAD_REQUEST.getStatusCode(),Response.Status.BAD_REQUEST.getReasonPhrase());
			errorLog.warn("Access limit exceeded");
			return exception.getException("Access limit exceeded",null);
		}
	}
	
	
	
}
