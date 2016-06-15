/**
 * 
 */
package com.mebelkart.api.order.v1.core;

/**
 * @author Tinku
 *
 */
public class OrderDetailSellerStatuses {
	
	private int vendorVisibility;
	private String statusName;
	private int activeState;

	/**
	 * Constructer
	 */
	public OrderDetailSellerStatuses(String statusName, int activeState, int vendorVisibility) {
		this.setStatusName(statusName);
		this.setActiveState(activeState);
		this.setVendorVisibility(vendorVisibility);
	}

	public int isVendorVisibility() {
		return vendorVisibility;
	}

	public void setVendorVisibility(int vendorVisibility) {
		this.vendorVisibility = vendorVisibility;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public int getActiveState() {
		return activeState;
	}

	public void setActiveState(int activeState) {
		this.activeState = activeState;
	}

}
