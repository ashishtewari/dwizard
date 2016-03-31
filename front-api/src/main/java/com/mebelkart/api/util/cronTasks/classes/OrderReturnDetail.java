package com.mebelkart.api.util.cronTasks.classes;

public class OrderReturnDetail {

	private Integer orderReturnId;
	private Integer orderDetailId;
	private Integer customizationId;
	private Integer productQuantity;
	
	public OrderReturnDetail(Integer orderReturnId, Integer orderDetailId,Integer customizationId, Integer productQuantity) {
		this.orderReturnId = orderReturnId;
		this.orderDetailId = orderDetailId;
		this.customizationId = customizationId;
		this.productQuantity = productQuantity;
	}

	public Integer getOrderReturnId() {
		return orderReturnId;
	}

	public void setOrderReturnId(Integer orderReturnId) {
		this.orderReturnId = orderReturnId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getCustomizationId() {
		return customizationId;
	}

	public void setCustomizationId(Integer customizationId) {
		this.customizationId = customizationId;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

}
