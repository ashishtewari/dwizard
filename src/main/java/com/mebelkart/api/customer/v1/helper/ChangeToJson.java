package com.mebelkart.api.customer.v1.helper;

public class ChangeToJson {

	public int statusCode;
	public String message;
	public Object result;

	/**
	 * It changes the result into json format.
	 * 
	 */
	public ChangeToJson(int statusCode, String message, Object result) {
		this.statusCode = statusCode;
		this.message = message;
		this.result=result;
	}

}
