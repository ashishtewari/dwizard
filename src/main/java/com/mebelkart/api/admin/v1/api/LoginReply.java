package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class LoginReply {
	/**
	 * status
	 */
	public int status;
    /**
     * @param status
     */
    public LoginReply(int status)
    {
    	this.status = status;
    }
}
