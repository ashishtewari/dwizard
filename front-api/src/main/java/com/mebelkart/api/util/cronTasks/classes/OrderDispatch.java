package com.mebelkart.api.util.cronTasks.classes;

public class OrderDispatch {

	private Integer orderId;
	private Integer dispatchNumber;
	

	public OrderDispatch(Integer orderId, Integer dispatchNumber) {
		this.orderId = orderId;
		this.dispatchNumber = dispatchNumber;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getDispatchNumber() {
		return dispatchNumber;
	}

	public void setDispatchNumber(Integer dispatchNumber) {
		this.dispatchNumber = dispatchNumber;
	}

}
