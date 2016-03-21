/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerAuthenticatonWrapper {

	private int customerId,isActive,isHavingGetPermission;

	public CustomerAuthenticatonWrapper(int customerId, int isActive, int isHavingPermission) {
		this.setCustomerId(customerId);
		this.setIsActive(isActive);
		this.setIsHavingGetPermission(isHavingPermission);
	}
	

	public int getIsHavingGetPermission() {
		return isHavingGetPermission;
	}

	public void setIsHavingGetPermission(int isHavingGetPermission) {
		this.isHavingGetPermission = isHavingGetPermission;
	}


	public int getCustomerId() {
		return customerId;
	}


	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}


	public int getIsActive() {
		return isActive;
	}


	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

}
