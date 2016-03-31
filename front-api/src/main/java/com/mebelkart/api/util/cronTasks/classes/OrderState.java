package com.mebelkart.api.util.cronTasks.classes;

public class OrderState {

	private Integer orderStateId;
	private boolean invoice;
	private boolean sendEmail;
	private String color;
	private boolean unRemovable;
	private boolean hidden;
	private boolean logable;
	private boolean delivery;
	private boolean deleted;
	
	

	public OrderState(Integer orderStateId, boolean invoice, boolean sendEmail,String color, boolean unRemovable, boolean hidden, boolean logable,boolean delivery, boolean deleted) {
		this.orderStateId = orderStateId;
		this.invoice = invoice;
		this.sendEmail = sendEmail;
		this.color = color;
		this.unRemovable = unRemovable;
		this.hidden = hidden;
		this.logable = logable;
		this.delivery = delivery;
		this.deleted = deleted;
	}

	public Integer getOrderStateId() {
		return orderStateId;
	}

	public void setOrderStateId(Integer orderStateId) {
		this.orderStateId = orderStateId;
	}

	public boolean isInvoice() {
		return invoice;
	}

	public void setInvoice(boolean invoice) {
		this.invoice = invoice;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public boolean isUnRemovable() {
		return unRemovable;
	}

	public void setUnRemovable(boolean unRemovable) {
		this.unRemovable = unRemovable;
	}

	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public boolean isLogable() {
		return logable;
	}

	public void setLogable(boolean logable) {
		this.logable = logable;
	}

	public boolean isDelivery() {
		return delivery;
	}

	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
