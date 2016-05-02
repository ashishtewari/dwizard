package com.mebelkart.api.util.classes;

/**
 * Created by vinitpayal on 20/04/16.
 */
public class InvalidInputReplyClass {
    private Integer status;
    private String message;
    private String description;

    public InvalidInputReplyClass(Integer status, String message, String description) {
        this.status = status;
        this.message = message;
        this.description = description;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
