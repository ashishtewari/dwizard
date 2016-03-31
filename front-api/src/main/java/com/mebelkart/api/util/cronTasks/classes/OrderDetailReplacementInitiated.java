package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailReplacementInitiated {
	private Integer orderDetailReplacementInitiatedId;
	private Integer orderDetailId;
	

	public OrderDetailReplacementInitiated(Integer orderDetailReplacementInitiatedId, Integer orderDetailId) {
		this.orderDetailReplacementInitiatedId = orderDetailReplacementInitiatedId;
		this.orderDetailId = orderDetailId;
	}

	public Integer getOrderDetailReplacementInitiatedId() {
		return orderDetailReplacementInitiatedId;
	}

	public void setOrderDetailReplacementInitiatedId(
			Integer orderDetailReplacementInitiatedId) {
		this.orderDetailReplacementInitiatedId = orderDetailReplacementInitiatedId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
}
