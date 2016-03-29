package com.mebelkart.api.util.cronTasks.classes;

public class OrderReturnState {

	private Integer orderReturnStateId;
	private String color;

	public OrderReturnState(Integer orderReturnStateId, String color) {
		this.orderReturnStateId = orderReturnStateId;
		this.color = color;
	}

	public Integer getOrderReturnStateId() {
		return orderReturnStateId;
	}

	public void setOrderReturnStateId(Integer orderReturnStateId) {
		this.orderReturnStateId = orderReturnStateId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
