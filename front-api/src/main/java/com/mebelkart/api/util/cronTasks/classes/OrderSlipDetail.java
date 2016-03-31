package com.mebelkart.api.util.cronTasks.classes;

public class OrderSlipDetail {

	private Integer orderSlipId;
	private Integer orderDetailId;
	private Integer productQuantity;
	

	public OrderSlipDetail(Integer orderSlipId, Integer orderDetailId,Integer productQuantity) {
		this.orderSlipId = orderSlipId;
		this.orderDetailId = orderDetailId;
		this.productQuantity = productQuantity;
	}

	public Integer getOrderSlipId() {
		return orderSlipId;
	}

	public void setOrderSlipId(Integer orderSlipId) {
		this.orderSlipId = orderSlipId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

}
