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
	 * It returns the null value exception to the user in json format
	 */
	public ChangeToJson getNullValueException() {
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Specify Content-Type Correctly";
		ChangeToJson nullValueExceptionResult = new ChangeToJson(statusCode, message, content);
		return nullValueExceptionResult;

	}

	/**
	 * It returns the query parameters null exception to the user in json format
	 */
	public ChangeToJson getNoQueryParamsException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " Page " + message;
		ChangeToJson noQueryParamsExceptionResult = new ChangeToJson(statusCode, message, content);
		return noQueryParamsExceptionResult;
	}

	/**
	 * It returns the unauthorized exception to the user in json format
	 */
	public ChangeToJson getUnAuthorizedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Invalid API key specified.";
		ChangeToJson unAuthorizedExceptionResult = new ChangeToJson(statusCode, message, content);
		return unAuthorizedExceptionResult;
	}

	/**
	 * It returns the api access limit exceeded exception to the user in json format
	 */
	public ChangeToJson getAccessLimitExceededException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You access limit has exceeded.";
		ChangeToJson accessLimitExceededExceptionResult = new ChangeToJson(statusCode, message, content);
		return accessLimitExceededExceptionResult;
	}

}
