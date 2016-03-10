package com.mebelkart.api.admin.v1.api;

public class Reply {
	public int status_code;
	public String message;
    public Object source;
    public Reply(int code,String msg,Object objToDisplay)
    {
    	this.status_code = code;
        this.message=msg;
        this.source=objToDisplay;
    }
}
