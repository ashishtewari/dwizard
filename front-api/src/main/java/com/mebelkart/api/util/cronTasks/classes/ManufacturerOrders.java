/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;


/**
 * @author Nikhil
 *
 */
public class ManufacturerOrders {
	private Integer orderId;
	private Integer manufacturerId;
	private String dateAdd;
	

	public ManufacturerOrders(Integer orderId, Integer manufacturerId,String dateAdd) {
		this.orderId = orderId;
		this.manufacturerId = manufacturerId;
		this.dateAdd = dateAdd;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(String dateAdd) {
		this.dateAdd = dateAdd;
	}

}
