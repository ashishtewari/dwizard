/**
 * 
 */
package com.mebelkart.api.other.v1.core;

/**
 * @author Tinku
 *
 */
public class DealsWrapper {

	private int productId;
	private String timeLeft;
	private String fsAvailability;
	/**
	 * Default Constructor
	 */
	public DealsWrapper(int productId,String timeLeft,String fsAvailability) {
		this.setProductId(productId);
		this.setTimeLeft(timeLeft);
		this.setFsAvailability(fsAvailability);
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getTimeLeft() {
		return timeLeft;
	}
	public void setTimeLeft(String timeLeft) {
		this.timeLeft = timeLeft;
	}
	public String getFsAvailability() {
		return fsAvailability;
	}
	public void setFsAvailability(String fsAvailability) {
		this.fsAvailability = fsAvailability;
	}

}
