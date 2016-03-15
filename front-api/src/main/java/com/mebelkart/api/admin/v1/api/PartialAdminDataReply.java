/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class PartialAdminDataReply {	
	/**
	 * status
	 */
	public int status;
	/**
	 * accessToken
	 */
	public String accessToken;
	/**
	 * password
	 */
	public String password;
	/**
	 * email
	 */
	public String email;
	/**
	 * The Default Constructer
	 */
	public PartialAdminDataReply(int status,String accessToken,String password,String email){
		this.status = status;
		this.accessToken = accessToken;
		this.password = password;
		this.email = email;		
	}
}
