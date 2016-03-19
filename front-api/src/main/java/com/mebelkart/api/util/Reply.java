package com.mebelkart.api.util;

/**
 * @author Tinku
 *
 */
public class Reply {
	/**
	 * status_code
	 */
	public int statusCode;
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
    	this.statusCode = code;
        this.message=msg;
        this.source=objToDisplay;
    }
}
