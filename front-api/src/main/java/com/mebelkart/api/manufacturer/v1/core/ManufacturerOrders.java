/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.core;



/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerOrders {
	private int manufacturerId;
	private int orderId;
	private int customerId;
	private int productId;
	private String productName;
	private double productPrice;
	private double totalDiscount;
	private double totalPaid;
	private double totalProducts;
	private double totalShipping;
	private String dateAdd;
	
	public ManufacturerOrders(){
		
	}
	

	public ManufacturerOrders(int manufacturerId, int orderId, int customerId,
			int productId, String productName, double productPrice,
			double totalDiscount, double totalPaid, double totalProducts,
			double totalShipping, String dateAdd) {
		this.manufacturerId = manufacturerId;
		this.orderId = orderId;
		this.customerId = customerId;
		this.productId = productId;
		this.productName = productName;
		this.productPrice = productPrice;
		this.totalDiscount = totalDiscount;
		this.totalPaid = totalPaid;
		this.totalProducts = totalProducts;
		this.totalShipping = totalShipping;
		this.dateAdd = dateAdd;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public double getTotalDiscount() {
		return totalDiscount;
	}

	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}

	public double getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(double totalPaid) {
		this.totalPaid = totalPaid;
	}

	public double getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(double totalProducts) {
		this.totalProducts = totalProducts;
	}

	public double getTotalShipping() {
		return totalShipping;
	}

	public void setTotalShipping(double totalShipping) {
		this.totalShipping = totalShipping;
	}

	public String getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(String dateAdd) {
		this.dateAdd = dateAdd;
	}

}
