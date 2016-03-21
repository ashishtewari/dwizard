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
}
