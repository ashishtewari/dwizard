/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class ConsumerResponse {
	
	/**
	 * accessToken
	 */
	public String accessToken;
	/**
	 * email
	 */
	public String email;
	/**
	 * The Default Constructer
	 */
	public ConsumerResponse(String accessToken,String email){
		this.accessToken = accessToken;
		this.email = email;
	}
}
