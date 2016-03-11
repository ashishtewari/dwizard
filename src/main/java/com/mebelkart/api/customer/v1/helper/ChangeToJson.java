package com.mebelkart.api.customer.v1.helper;

public class ChangeToJson {

	public Object result;
	public int statusCode;
	public String message;

	public ChangeToJson(int statusCode, String message, Object result) {
		this.result=result;
		this.statusCode = (statusCode);
		this.message = (message);
	}
	public ChangeToJson(int statusCode, String message, String result) {
		this.result=result;
		this.statusCode = (statusCode);
		this.message = (message);
	}

}
