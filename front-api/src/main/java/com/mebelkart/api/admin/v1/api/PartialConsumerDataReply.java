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
	 * status
	 */
	public int status;
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
	public PartialConsumerDataReply(int status,String accessToken,String email){
		this.status = status;
		this.accessToken = accessToken;
		this.email = email;
	}
}
