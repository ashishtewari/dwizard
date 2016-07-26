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
	private String productName;
	private String productImage;
	private int catId;
	private int mktPrice;
	private int ourPrice;
	private String flashSaleEndDate;
	private String flashSaleEndsIn;
	private String fsAvailability;
	/**
	 * Default Constructor
	 */
	public DealsWrapper(int productId,String flashSaleEndDate,String fsAvailability) {
		this.setProductId(productId);
		this.setFlashSaleEndDate(flashSaleEndDate);
		this.setFsAvailability(fsAvailability);
	}
	public DealsWrapper(){
		// empty constructor;
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public String getFlashSaleEndDate() {
		return flashSaleEndDate;
	}
	public void setFlashSaleEndDate(String flashSaleEndDate) {
		this.flashSaleEndDate = flashSaleEndDate;
	}
	public String getFsAvailability() {
		return fsAvailability;
	}
	public void setFsAvailability(String fsAvailability) {
		this.fsAvailability = fsAvailability;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getProductImage() {
		return productImage;
	}
	public void setProductImage(String productImage) {
		this.productImage = productImage;
	}
	public int getOurPrice() {
		return ourPrice;
	}
	public void setOurPrice(int ourPrice) {
		this.ourPrice = ourPrice;
	}
	public int getMktPrice() {
		return mktPrice;
	}
	public void setMktPrice(int mktPrice) {
		this.mktPrice = mktPrice;
	}
	public int getCatId() {
		return catId;
	}
	public void setCatId(int catId) {
		this.catId = catId;
	}
	public String getFlashSaleEndsIn() {
		return flashSaleEndsIn;
	}
	public void setFlashSaleEndsIn(String flashSaleEndsIn) {
		this.flashSaleEndsIn = flashSaleEndsIn;
	}

}
