package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailRefundInitiated {
	private Integer orderDetailRefundInitiatedId;
	private Integer orderDetailId;
	

	public OrderDetailRefundInitiated(Integer orderDetailRefundInitiatedId,Integer orderDetailId) {
		this.orderDetailRefundInitiatedId = orderDetailRefundInitiatedId;
		this.orderDetailId = orderDetailId;
	}

	public Integer getOrderDetailRefundInitiatedId() {
		return orderDetailRefundInitiatedId;
	}

	public void setOrderDetailRefundInitiatedId(Integer orderDetailRefundInitiatedId) {
		this.orderDetailRefundInitiatedId = orderDetailRefundInitiatedId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

}
