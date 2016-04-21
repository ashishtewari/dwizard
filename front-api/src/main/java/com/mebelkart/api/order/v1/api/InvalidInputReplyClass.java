package com.mebelkart.api.order.v1.api;

/**
 * Created by vinitpayal on 20/04/16.
 */
public class InvalidInputReplyClass {
    private Integer status;
    private String message;

    public InvalidInputReplyClass(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
