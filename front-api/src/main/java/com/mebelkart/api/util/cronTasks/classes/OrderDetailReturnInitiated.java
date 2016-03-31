package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailReturnInitiated {
	private Integer returnInitiatedId;
	private Integer orderDetailId;
	private Integer exceptionForwardActionId;
	
	

	public OrderDetailReturnInitiated(Integer returnInitiatedId,Integer orderDetailId, Integer exceptionForwardActionId) {
		this.returnInitiatedId = returnInitiatedId;
		this.orderDetailId = orderDetailId;
		this.exceptionForwardActionId = exceptionForwardActionId;
	}

	public Integer getReturnInitiatedId() {
		return returnInitiatedId;
	}

	public void setReturnInitiatedId(Integer returnInitiatedId) {
		this.returnInitiatedId = returnInitiatedId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getExceptionForwardActionId() {
		return exceptionForwardActionId;
	}

	public void setExceptionForwardActionId(Integer exceptionForwardActionId) {
		this.exceptionForwardActionId = exceptionForwardActionId;
	}

}
