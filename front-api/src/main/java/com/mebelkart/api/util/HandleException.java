/**
 * 
 */
package com.mebelkart.api.util;

/**
 * @author Nikky-Akky
 *
 */
public class HandleException {

	private int statusCode;
	private String message;
	
	public HandleException() {
		// TODO Auto-generated constructor stub
		this.statusCode = 0;
		this.message = null;
	} 

	public HandleException(int statusCode, String message) {
		// TODO Auto-generated constructor stub
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
	 * @return It returns the null value exception to the user in json format
	 */
	public Reply getContentTypeNullValueException() {
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Specify Content-Type or apikey or customerid Correctly";
		Reply nullValueExceptionResult = new Reply(statusCode, message, content);
		return nullValueExceptionResult;

	}

	/**
	 * @return It returns the query parameters null exception to the user in json format
	 */
	public Reply getNoQueryParamsException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " Page " + message;
		Reply noQueryParamsExceptionResult = new Reply(statusCode, message, content);
		return noQueryParamsExceptionResult;
	}

	/**
	 * @return It returns the unauthorized exception to the user in json format
	 */
	public Reply getUnAuthorizedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Invalid API key specified or key is inActive.";
		Reply unAuthorizedExceptionResult = new Reply(statusCode, message, content);
		return unAuthorizedExceptionResult;
	}

	/**
	 * @return It returns the api access limit exceeded exception to the user in json format
	 */
	public Reply getAccessLimitExceededException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You access limit has exceeded.";
		Reply accessLimitExceededExceptionResult = new Reply(statusCode, message, content);
		return accessLimitExceededExceptionResult;
	}

	/**
	 * @return method not allowed exception for un authorized users for a particular method
	 */
	public Reply getMethodNotAllowedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You are not authorized to perform this action";
		Reply methodNotAllowedExceptionResult = new Reply(statusCode, message, content);
		return methodNotAllowedExceptionResult;
	}

	/**
	 * @return customerId is not valid exception if customer id mentioned is not valid
	 */
	public Reply getCustomerIdNotValidException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", CustomerId you mentioned was invalid";
		Reply customerIdNotValidExceptionResult = new Reply(statusCode, message, content);
		return customerIdNotValidExceptionResult;
	}

	/**
	 * @return
	 */
	public Reply getCustomerIdNullException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", customerId should not be null";
		Reply customerIdNullExceptionResult = new Reply(statusCode, message, content);
		return customerIdNullExceptionResult;
	}

}
