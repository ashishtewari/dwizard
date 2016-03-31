package com.mebelkart.api.util.cronTasks.classes;

public class OrderTax {

	private Integer orderId;
	private String taxName;
	private double taxRate;
	private double amount;
	
	
	public OrderTax(Integer orderId, String taxName, double taxRate,double amount) {
		this.orderId = orderId;
		this.taxName = taxName;
		this.taxRate = taxRate;
		this.amount = amount;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getTaxName() {
		return taxName;
	}

	public void setTaxName(String taxName) {
		this.taxName = taxName;
	}

	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
