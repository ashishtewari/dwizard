/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.core;


/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerDetailsWrapper {
	//@ColumnName("id_manufacturer")
	private int manufacturerId;
	//@ColumnName("id_product")
	private int productId;

	public ManufacturerDetailsWrapper(int manufacturerId, int productId) {
		this.manufacturerId = manufacturerId;
		this.productId = productId;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

}
