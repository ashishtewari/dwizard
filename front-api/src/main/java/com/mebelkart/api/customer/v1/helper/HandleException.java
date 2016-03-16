/**
 * 
 */
package com.mebelkart.api.customer.v1.helper;

/**
 * @author Nikky-Akky
 *
 */
public class HandleException {

	private int statusCode;
	private String message;

	public HandleException(int statusCode, String message) {
		// TODO Auto-generated constructor stub
		this.statusCode = statusCode;
		this.message = message;
	}

	/**
	 * @return It returns the null value exception to the user in json format
	 */
	public ChangeToJson getContentTypeNullValueException() {
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Specify Content-Type or apikey or customerid Correctly";
		ChangeToJson nullValueExceptionResult = new ChangeToJson(statusCode, message, content);
		return nullValueExceptionResult;

	}

	/**
	 * @return It returns the query parameters null exception to the user in json format
	 */
	public ChangeToJson getNoQueryParamsException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " Page " + message;
		ChangeToJson noQueryParamsExceptionResult = new ChangeToJson(statusCode, message, content);
		return noQueryParamsExceptionResult;
	}

	/**
	 * @return It returns the unauthorized exception to the user in json format
	 */
	public ChangeToJson getUnAuthorizedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Invalid API key specified or key is inActive.";
		ChangeToJson unAuthorizedExceptionResult = new ChangeToJson(statusCode, message, content);
		return unAuthorizedExceptionResult;
	}

	/**
	 * @return It returns the api access limit exceeded exception to the user in json format
	 */
	public ChangeToJson getAccessLimitExceededException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You access limit has exceeded.";
		ChangeToJson accessLimitExceededExceptionResult = new ChangeToJson(statusCode, message, content);
		return accessLimitExceededExceptionResult;
	}

	/**
	 * @return method not allowed exception for un authorized users for a particular method
	 */
	public ChangeToJson getMethodNotAllowedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You are not authorized to perform this action";
		ChangeToJson methodNotAllowedExceptionResult = new ChangeToJson(statusCode, message, content);
		return methodNotAllowedExceptionResult;
	}

	/**
	 * @return customerId is not valid exception if customer id mentioned is not valid
	 */
	public ChangeToJson getCustomerIdNotValidException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", CustomerId you mentioned was invalid";
		ChangeToJson customerIdNotValidExceptionResult = new ChangeToJson(statusCode, message, content);
		return customerIdNotValidExceptionResult;
	}

	/**
	 * @return
	 */
	public ChangeToJson getCustomerIdNullException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", customerId should not be null";
		ChangeToJson customerIdNullExceptionResult = new ChangeToJson(statusCode, message, content);
		return customerIdNullExceptionResult;
	}

}
