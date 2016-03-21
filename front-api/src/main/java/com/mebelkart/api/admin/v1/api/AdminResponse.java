/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class AdminResponse {	
	
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
	public AdminResponse(String accessToken,String password,String email){
		this.accessToken = accessToken;
		this.password = password;
		this.email = email;		
	}
}
