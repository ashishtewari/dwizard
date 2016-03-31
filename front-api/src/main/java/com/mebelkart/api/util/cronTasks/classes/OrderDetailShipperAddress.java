package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailShipperAddress {

	private Integer orderDetailId;
	private Integer shipperAddressId;
	private boolean active;
	

	public OrderDetailShipperAddress(Integer orderDetailId,Integer shipperAddressId, boolean active) {
		this.orderDetailId = orderDetailId;
		this.shipperAddressId = shipperAddressId;
		this.active = active;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getShipperAddressId() {
		return shipperAddressId;
	}

	public void setShipperAddressId(Integer shipperAddressId) {
		this.shipperAddressId = shipperAddressId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
