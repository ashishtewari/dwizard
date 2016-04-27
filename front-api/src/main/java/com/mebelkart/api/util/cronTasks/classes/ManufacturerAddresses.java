package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class ManufacturerAddresses {

	private Integer addressId;
	private boolean isDefault;
	private boolean active;
	private Integer manufacturerId;
	private Integer countryId;
	private Integer stateId;
	private Integer supplierId;
	private String alias;
	private String firstName;
	private String lastName;
	private String address1;
	private String address2;
	private String postCode;
	private String city;
	private String other;
	private String phone;
	private String mobile;
	private Date dateAdded;
	private Date dateUpdated;
	
	
	public ManufacturerAddresses(Integer addressId, boolean isDefault,
			boolean active,Integer manufacturerId, Integer countryId, Integer stateId,
			Integer supplierId, String alias, String firstName,
			String lastName, String address1, String address2, String postCode,
			String city, String other, String phone, String mobile,
			Date dateAdded, Date dateUpdated) {
		this.addressId = addressId;
		this.isDefault = isDefault;
		this.active = active;
		this.manufacturerId = manufacturerId;
		this.countryId = countryId;
		this.stateId = stateId;
		this.supplierId = supplierId;
		this.alias = alias;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address1 = address1;
		this.address2 = address2;
		this.postCode = postCode;
		this.city = city;
		this.other = other;
		this.phone = phone;
		this.mobile = mobile;
		this.dateAdded = dateAdded;
		this.dateUpdated = dateUpdated;
	}

	public Integer getCountryId() {
		return countryId;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(Date dateUpdated) {
		this.dateUpdated = dateUpdated;
	}

	public Integer getAddressId() {
		return addressId;
	}

	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

}
