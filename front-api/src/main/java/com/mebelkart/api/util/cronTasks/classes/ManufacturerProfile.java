/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;

/**
 * @author Nikhil
 *
 */
public class ManufacturerProfile {

	private Integer manufacturerProfile;
	private Integer employeeId;
	private Integer manufacturerId;
	
	public ManufacturerProfile(Integer manufacturerProfile, Integer employeeId,
			Integer manufacturerId) {
		this.manufacturerProfile = manufacturerProfile;
		this.employeeId = employeeId;
		this.manufacturerId = manufacturerId;
	}

	public Integer getManufacturerProfile() {
		return manufacturerProfile;
	}

	public void setManufacturerProfile(Integer manufacturerProfile) {
		this.manufacturerProfile = manufacturerProfile;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

}
