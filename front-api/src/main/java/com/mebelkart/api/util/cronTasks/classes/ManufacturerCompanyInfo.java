package com.mebelkart.api.util.cronTasks.classes;

public class ManufacturerCompanyInfo {

	private String companyName;
	private String companyDescription;
	private String displayName;
	private String companyPolicy;
	private boolean active;
	

	public ManufacturerCompanyInfo(String companyName,
			String companyDescription, String displayName,
			String companyPolicy, boolean active) {
		this.companyName = companyName;
		this.companyDescription = companyDescription;
		this.displayName = displayName;
		this.companyPolicy = companyPolicy;
		this.active = active;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyDescription() {
		return companyDescription;
	}

	public void setCompanyDescription(String companyDescription) {
		this.companyDescription = companyDescription;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getCompanyPolicy() {
		return companyPolicy;
	}

	public void setCompanyPolicy(String companyPolicy) {
		this.companyPolicy = companyPolicy;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
