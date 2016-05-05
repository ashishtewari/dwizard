/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;

/**
 * @author Nikhil
 *
 */
public class ManufacturerBankAccountInfo {

	private Integer manufacturerId;
	private String accountName;
	private String bankName;
	private Integer accountType;
	private String accountNumber;
	private String branchCity;
	private String ifscCode;
	private boolean active;
	
	

	public ManufacturerBankAccountInfo(Integer manufacturerId,
			String accountName, String bankName, Integer accountType,
			String accountNumber, String branchCity, String ifscCode,
			boolean active) {
		this.manufacturerId = manufacturerId;
		this.accountName = accountName;
		this.bankName = bankName;
		this.accountType = accountType;
		this.accountNumber = accountNumber;
		this.branchCity = branchCity;
		this.ifscCode = ifscCode;
		this.active = active;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Integer getAccountType() {
		return accountType;
	}

	public void setAccountType(Integer accountType) {
		this.accountType = accountType;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getBranchCity() {
		return branchCity;
	}

	public void setBranchCity(String branchCity) {
		this.branchCity = branchCity;
	}

	public String getIfscCode() {
		return ifscCode;
	}

	public void setIfscCode(String ifscCode) {
		this.ifscCode = ifscCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
