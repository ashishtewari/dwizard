package com.mebelkart.api.customer.v1.helper;



public class ChangeToJson {

	public int statusCode;
	public String message;
	public Object source;

	/**
	 * @param statusCode
	 * @param message
	 * @param content
	 */
	public ChangeToJson(int statusCode, String message, String source) {
		// TODO Auto-generated constructor stub
		this.statusCode = statusCode;
		this.message = message;
		this.source = source;
	}
	
	public ChangeToJson(int statusCode, String message, Object source) {
		// TODO Auto-generated constructor stub
		this.statusCode = statusCode;
		this.message = message;
		this.source = source;
	}

}
