package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;
import java.util.List;

public class Manufacturer {
	private Integer manufacturerId;
	private String name;
	private Date dateAdd;
	private Date dateUpd;
	private boolean active;
	private String email;
	private Integer isCommisionableVendor;
	private Integer isWholesaleVendor;
	private List<ManufacturerAddresses> manufacturerAddresses;
	private List<ManufacturerProducts> manufacturerProducts;
	private List<ManufacturerCompanyInfo> manufacturerCompanyInfo;
	private List<Object> manufacturerOrders;
	
	public Manufacturer(Integer manufacturerId, String name, Date dateAdd,
			Date dateUpd, boolean active, String email,
			Integer isCommisionableVendor, Integer isWholesaleVendor,
			List<ManufacturerAddresses>manufacturerAddresses,List<ManufacturerProducts> manufacturerProducts,
			List<ManufacturerCompanyInfo>manufacturerCompanyInfo,List<Object> manufacturerOrders){
		this.manufacturerId = manufacturerId;
		this.name = name;
		this.dateAdd = dateAdd;
		this.dateUpd = dateUpd;
		this.active = active;
		this.email = email;
		this.isCommisionableVendor = isCommisionableVendor;
		this.isWholesaleVendor = isWholesaleVendor;
		this.manufacturerAddresses = manufacturerAddresses;
		this.manufacturerProducts = manufacturerProducts;
		this.manufacturerCompanyInfo = manufacturerCompanyInfo;
		this.manufacturerOrders = manufacturerOrders;
	}

	/**
	 * 
	 */
	public Manufacturer() {
		
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getIsCommisionableVendor() {
		return isCommisionableVendor;
	}

	public void setIsCommisionableVendor(Integer isCommisionableVendor) {
		this.isCommisionableVendor = isCommisionableVendor;
	}

	public Integer getIsWholesaleVendor() {
		return isWholesaleVendor;
	}

	public void setIsWholesaleVendor(Integer isWholesaleVendor) {
		this.isWholesaleVendor = isWholesaleVendor;
	}

	public List<ManufacturerAddresses> getManufacturerAddresses() {
		return manufacturerAddresses;
	}

	public void setManufacturerAddresses(List<ManufacturerAddresses> manufacturerAddresses) {
		this.manufacturerAddresses = manufacturerAddresses;
	}

	public List<ManufacturerProducts> getManufacturerProducts() {
		return manufacturerProducts;
	}

	public void setManufacturerProducts(List<ManufacturerProducts> manufacturerProducts) {
		this.manufacturerProducts = manufacturerProducts;
	}

	public List<Object> getManufacturerOrders() {
		return manufacturerOrders;
	}

	public void setManufacturerOrders(List<Object> manufacturerOrders) {
		this.manufacturerOrders = manufacturerOrders;
	}

	public List<ManufacturerCompanyInfo> getManufacturerCompanyInfo() {
		return manufacturerCompanyInfo;
	}

	public void setManufacturerCompanyInfo(
			List<ManufacturerCompanyInfo> manufacturerCompanyInfo) {
		this.manufacturerCompanyInfo = manufacturerCompanyInfo;
	}

}
