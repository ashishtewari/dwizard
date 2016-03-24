/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerAuthenticatonWrapper {

	private int customerId;
	private int isActive;
	private int isHavingGetPermission;
	private int isHavingPostPermission;
	private int isHavingPutPermission;
	private int isHavingDeletePermission;

	public int getIsHavingPutPermission() {
		return isHavingPutPermission;
	}


	public void setIsHavingPutPermission(int isHavingPutPermission) {
		this.isHavingPutPermission = isHavingPutPermission;
	}


	public int getIsHavingDeletePermission() {
		return isHavingDeletePermission;
	}


	public void setIsHavingDeletePermission(int isHavingDeletePermission) {
		this.isHavingDeletePermission = isHavingDeletePermission;
	}


	public CustomerAuthenticatonWrapper(int customerId, int isActive, int isHavingGetPermission,int isHavingPostPermission,int isHavingPutPermission,int isHavingDeletePermission) {
		this.setCustomerId(customerId);
		this.setIsActive(isActive);
		this.setIsHavingGetPermission(isHavingGetPermission);
		this.setIsHavingPostPermission(isHavingPostPermission);
		this.setIsHavingPutPermission(isHavingPutPermission);
		this.setIsHavingDeletePermission(isHavingDeletePermission);
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


	public int getIsHavingPostPermission() {
		return isHavingPostPermission;
	}


	public void setIsHavingPostPermission(int isHavingPostPermission) {
		this.isHavingPostPermission = isHavingPostPermission;
	}

}
