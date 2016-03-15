package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class SubStatusReply {
	/**
	 * status
	 */
	public int status;
	/**
	 * msg
	 */
	public String msg;
    /**
     * @param status
     */
    public SubStatusReply(int status,String msg)
    {
    	this.status = status;
    	this.msg = msg;
    }
}
