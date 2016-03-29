package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailStatus {

	private Integer orderDetailStatusId;
	private String statusName;
	private boolean active;
	

	public OrderDetailStatus(Integer orderDetailStatusId, String statusName,boolean active) {
		this.orderDetailStatusId = orderDetailStatusId;
		this.statusName = statusName;
		this.active = active;
	}

	public Integer getOrderDetailStatusId() {
		return orderDetailStatusId;
	}

	public void setOrderDetailStatusId(Integer orderDetailStatusId) {
		this.orderDetailStatusId = orderDetailStatusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
