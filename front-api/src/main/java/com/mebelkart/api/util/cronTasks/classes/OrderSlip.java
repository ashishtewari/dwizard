package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderSlip {

	private Integer orderSlipId;
	private double conversionRate;
	private Integer customerId;
	private Integer orderId;
	private boolean shippingCost;
	private Date dateAdd;
	private Date dateUpd;
	
	
	public OrderSlip(Integer orderSlipId, double conversionRate,Integer customerId, Integer orderId, boolean shippingCost,Date dateAdd, Date dateUpd) {
		this.orderSlipId = orderSlipId;
		this.conversionRate = conversionRate;
		this.customerId = customerId;
		this.orderId = orderId;
		this.shippingCost = shippingCost;
		this.dateAdd = dateAdd;
		this.dateUpd = dateUpd;
	}

	public Integer getOrderSlipId() {
		return orderSlipId;
	}

	public void setOrderSlipId(Integer orderSlipId) {
		this.orderSlipId = orderSlipId;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public boolean isShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(boolean shippingCost) {
		this.shippingCost = shippingCost;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public Date getDateUpd() {
		return dateUpd;
	}

	public void setDateUpd(Date dateUpd) {
		this.dateUpd = dateUpd;
	}

}
