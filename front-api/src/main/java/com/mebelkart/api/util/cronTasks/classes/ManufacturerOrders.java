/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

/**
 * @author Nikhil
 *
 */
public class ManufacturerOrders {
	private Integer orderId;
	private Integer manufacturerId;
	private Date dateAdd;
	

	public ManufacturerOrders(Integer orderId, Integer manufacturerId,Date dateAdd) {
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

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

}
