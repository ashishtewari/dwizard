/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class PartialConsumerDataReply {
	
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
	public PartialConsumerDataReply(String accessToken,String email){
		this.accessToken = accessToken;
		this.email = email;
	}
}
