package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class Reply {
	/**
	 * status_code
	 */
	public int status_code;
	/**
	 * message
	 */
	public String message;
    /**
     * source
     */
    public Object source;
    /**
     * @param code
     * @param msg
     * @param objToDisplay
     */
    public Reply(int code,String msg,Object objToDisplay)
    {
    	this.status_code = code;
        this.message=msg;
        this.source=objToDisplay;
    }
}
