package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailReturnRejected {
	
	private Integer orderDetailReturnRejectedId;
	private Integer orderDetailId;
	
	
	public OrderDetailReturnRejected(Integer orderDetailReturnRejectedId,Integer orderDetailId) {
		this.orderDetailReturnRejectedId = orderDetailReturnRejectedId;
		this.orderDetailId = orderDetailId;
	}
	
	public Integer getOrderDetailReturnRejectedId() {
		return orderDetailReturnRejectedId;
	}
	public void setOrderDetailReturnRejectedId(Integer orderDetailReturnRejectedId) {
		this.orderDetailReturnRejectedId = orderDetailReturnRejectedId;
	}
	public Integer getOrderDetailId() {
		return orderDetailId;
	}
	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
	

}
