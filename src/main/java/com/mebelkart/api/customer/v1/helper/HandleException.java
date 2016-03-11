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

	public ChangeToJson getNullValueException() {
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Specify Content-Type Correctly";
		ChangeToJson result = new ChangeToJson(statusCode, message, content);
		return result;

	}


	public ChangeToJson getNoQueryParamsException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " Page " + message;
		ChangeToJson result = new ChangeToJson(statusCode, message, content);
		return result;
	}

	public ChangeToJson getUnAuthorizedException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", Invalid API key specified.";
		ChangeToJson result = new ChangeToJson(statusCode, message, content);
		return result;
	}

	public ChangeToJson getAccessLimitExceededException() {
		// TODO Auto-generated method stub
		String content = "";
		message = "HTTP " + statusCode + " " + message
				+ ", You access limit has exceeded.";
		ChangeToJson result = new ChangeToJson(statusCode, message, content);
		return result;
	}

}
