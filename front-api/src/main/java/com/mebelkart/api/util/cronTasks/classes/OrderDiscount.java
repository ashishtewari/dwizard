package com.mebelkart.api.util.cronTasks.classes;

public class OrderDiscount {

	private Integer orderDiscountId;
	private Integer orderId;
	private Integer discountId;
	private String name;
	private double value;
	

	public OrderDiscount(Integer orderDiscountId, Integer orderId,Integer discountId, String name, double value) {
		this.orderDiscountId = orderDiscountId;
		this.orderId = orderId;
		this.discountId = discountId;
		this.name = name;
		this.value = value;
	}

	public Integer getOrderDiscountId() {
		return orderDiscountId;
	}

	public void setOrderDiscountId(Integer orderDiscountId) {
		this.orderDiscountId = orderDiscountId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getDiscountId() {
		return discountId;
	}

	public void setDiscountId(Integer discountId) {
		this.discountId = discountId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
