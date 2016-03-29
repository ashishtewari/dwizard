package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailExceptionForwardAction {

	private Integer orderDetailReturnInitiatedId;
	private Integer exceptionForwardActionId;
	
	
	public OrderDetailExceptionForwardAction(Integer orderDetailReturnInitiatedId,Integer exceptionForwardActionId) {
		this.orderDetailReturnInitiatedId = orderDetailReturnInitiatedId;
		this.exceptionForwardActionId = exceptionForwardActionId;
	}

	public Integer getOrderDetailReturnInitiatedId() {
		return orderDetailReturnInitiatedId;
	}

	public void setOrderDetailReturnInitiatedId(
			Integer orderDetailReturnInitiatedId) {
		this.orderDetailReturnInitiatedId = orderDetailReturnInitiatedId;
	}

	public Integer getExceptionForwardActionId() {
		return exceptionForwardActionId;
	}

	public void setExceptionForwardActionId(Integer exceptionForwardActionId) {
		this.exceptionForwardActionId = exceptionForwardActionId;
	}

}
