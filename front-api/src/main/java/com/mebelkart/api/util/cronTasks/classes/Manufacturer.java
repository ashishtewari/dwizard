package com.mebelkart.api.util.cronTasks.classes;


public class Manufacturer {
	private Integer manufacturerId;
	private String name;
	private String dateAdd;
	private String dateUpd;
	private boolean active;
	private String email;
	private Integer isCommisionableVendor;
	private Integer isWholesaleVendor;
	private ManufacturerCompanyInfo manufacturerCompanyInfo;
	private ManufacturerBankAccountInfo manufacturerBankAccountInfo;
	private ManufacturerLang manufacturerLang;
	private ManufacturerProfile manufacturerProfile;
	
	
	public ManufacturerBankAccountInfo getManufacturerBankAccountInfo() {
		return manufacturerBankAccountInfo;
	}

	public void setManufacturerBankAccountInfo(
			ManufacturerBankAccountInfo manufacturerBankAccountInfo) {
		this.manufacturerBankAccountInfo = manufacturerBankAccountInfo;
	}

	public ManufacturerLang getManufacturerLang() {
		return manufacturerLang;
	}

	public void setManufacturerLang(ManufacturerLang manufacturerLang) {
		this.manufacturerLang = manufacturerLang;
	}

	public ManufacturerProfile getManufacturerProfile() {
		return manufacturerProfile;
	}

	public void setManufacturerProfile(ManufacturerProfile manufacturerProfile) {
		this.manufacturerProfile = manufacturerProfile;
	}

	public Manufacturer(Integer manufacturerId, String name, String dateAdd,
			String dateUpd, boolean active, String email,
			Integer isCommisionableVendor, Integer isWholesaleVendor,
			ManufacturerCompanyInfo manufacturerCompanyInfo){
		this.manufacturerId = manufacturerId;
		this.name = name;
		this.dateAdd = dateAdd;
		this.dateUpd = dateUpd;
		this.active = active;
		this.email = email;
		this.isCommisionableVendor = isCommisionableVendor;
		this.isWholesaleVendor = isWholesaleVendor;
		this.manufacturerCompanyInfo = manufacturerCompanyInfo;
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

	public String getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(String dateAdd) {
		this.dateAdd = dateAdd;
	}

	public String getDateUpd() {
		return dateUpd;
	}

	public void setDateUpd(String dateUpd) {
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

	public ManufacturerCompanyInfo getManufacturerCompanyInfo() {
		return manufacturerCompanyInfo;
	}

	public void setManufacturerCompanyInfo(
			ManufacturerCompanyInfo manufacturerCompanyInfo) {
		this.manufacturerCompanyInfo = manufacturerCompanyInfo;
	}

}
