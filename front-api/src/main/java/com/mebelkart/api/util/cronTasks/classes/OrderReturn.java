package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderReturn {

	private Integer orderReturnId;
	private Integer customerId;
	private Integer orderId;
	private boolean state;
	private String question;
	private Date dateAdd;
	private Date dateUpd;
	

	public OrderReturn(Integer orderReturnId, Integer customerId,Integer orderId, boolean state, String question, Date dateAdd,Date dateUpd) {
		this.orderReturnId = orderReturnId;
		this.customerId = customerId;
		this.orderId = orderId;
		this.state = state;
		this.question = question;
		this.dateAdd = dateAdd;
		this.dateUpd = dateUpd;
	}

	public Integer getOrderReturnId() {
		return orderReturnId;
	}

	public void setOrderReturnId(Integer orderReturnId) {
		this.orderReturnId = orderReturnId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public Date getDateUpd() {
		return dateUpd;
	}

	public void setDateUpd(Date dateUpd) {
		this.dateUpd = dateUpd;
	}

}
