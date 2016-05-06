package com.mebelkart.api.util.classes;

/**
 * @author Tinku
 *
 */
public class Reply {
	/**
	 * status_code
	 */
	public int status;
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
    	this.status = code;
        this.message=msg;
        this.source=objToDisplay;
    }
}
