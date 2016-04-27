/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerProducts {
	private Integer productId;
	private Integer categoryId;
	private double price;
	private String productName;
	private Integer manufacturerId;
	
	public ManufacturerProducts(Integer productId,Integer categoryId,double price,String productName,Integer manufacturerId ) {
		this.productId = productId;
		this.categoryId = categoryId;
		this.price = price;
		this.productName = productName;
		this.manufacturerId = manufacturerId;
	}
	

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}


	public double getPrice() {
		return price;
	}


	public void setPrice(double price) {
		this.price = price;
	}


	public String getProductName() {
		return productName;
	}


	public void setProductName(String productName) {
		this.productName = productName;
	}

}
