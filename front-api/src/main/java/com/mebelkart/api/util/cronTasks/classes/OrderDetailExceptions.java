package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderDetailExceptions {
	private Integer orderDetailExceptionId;
	private Integer orderDetailId;
	private boolean isSolved;
	private Date exceptionDateAdd;
	private String exceptionComments;
	private Integer exceptionReasonId;
	private Integer hasAttachment;
	private boolean active;
	

	public OrderDetailExceptions(Integer orderDetailExceptionId,
			Integer orderDetailId, boolean isSolved, Date exceptionDateAdd,
			String exceptionComments, Integer exceptionReasonId,
			Integer hasAttachment, boolean active) {
		this.orderDetailExceptionId = orderDetailExceptionId;
		this.orderDetailId = orderDetailId;
		this.isSolved = isSolved;
		this.exceptionDateAdd = exceptionDateAdd;
		this.exceptionComments = exceptionComments;
		this.exceptionReasonId = exceptionReasonId;
		this.hasAttachment = hasAttachment;
		this.active = active;
	}

	public Integer getOrderDetailExceptionId() {
		return orderDetailExceptionId;
	}

	public void setOrderDetailExceptionId(Integer orderDetailExceptionId) {
		this.orderDetailExceptionId = orderDetailExceptionId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public boolean isSolved() {
		return isSolved;
	}

	public void setSolved(boolean isSolved) {
		this.isSolved = isSolved;
	}

	public Date getExceptionDateAdd() {
		return exceptionDateAdd;
	}

	public void setExceptionDateAdd(Date exceptionDateAdd) {
		this.exceptionDateAdd = exceptionDateAdd;
	}

	public String getExceptionComments() {
		return exceptionComments;
	}

	public void setExceptionComments(String exceptionComments) {
		this.exceptionComments = exceptionComments;
	}

	public Integer getExceptionReasonId() {
		return exceptionReasonId;
	}

	public void setExceptionReasonId(Integer exceptionReasonId) {
		this.exceptionReasonId = exceptionReasonId;
	}

	public Integer getHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(Integer hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
