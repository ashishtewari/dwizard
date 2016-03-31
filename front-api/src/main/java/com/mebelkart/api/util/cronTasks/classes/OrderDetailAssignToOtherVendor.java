package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailAssignToOtherVendor {
	private Integer orderDetailId;
	private Integer fromVendorId;
	private Integer toVendorId;
	

	public OrderDetailAssignToOtherVendor(Integer orderDetailId,Integer fromVendorId, Integer toVendorId) {
		this.orderDetailId = orderDetailId;
		this.fromVendorId = fromVendorId;
		this.toVendorId = toVendorId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getFromVendorId() {
		return fromVendorId;
	}

	public void setFromVendorId(Integer fromVendorId) {
		this.fromVendorId = fromVendorId;
	}

	public Integer getToVendorId() {
		return toVendorId;
	}

	public void setToVendorId(Integer toVendorId) {
		this.toVendorId = toVendorId;
	}

}
