/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.core;

/**
 * @author Tinku
 *
 */
public class WishListProductsWrapper {

	private int productId;
	private int quantity;
	private int productQuantity;
	private String name;
	private int productAttributeId;
	private int priority;
	private String linkRewrite;
	private String categoryRewrite;
	private int attributeQuantity;
	private String attributeSmall;
	/**
	 * Constructor
	 */
	public WishListProductsWrapper(int proId,int qua,int proQuan,String name, int proAttId,int priority,String linkRewrite,String catRewrite) {
		this.setProductId(proId);
		this.setQuantity(qua);
		this.setProductQuantity(proQuan);
		this.setName(name);
		this.setProductAttributeId(proAttId);
		this.setPriority(priority);
		this.setLinkRewrite(linkRewrite);
		this.setCategoryRewrite(catRewrite);
	}
	public int getProductId() {
		return productId;
	}
	public void setProductId(int productId) {
		this.productId = productId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getProductQuantity() {
		return productQuantity;
	}
	public void setProductQuantity(int productQuantity) {
		this.productQuantity = productQuantity;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getProductAttributeId() {
		return productAttributeId;
	}
	public void setProductAttributeId(int productAttributeId) {
		this.productAttributeId = productAttributeId;
	}
	public String getLinkRewrite() {
		return linkRewrite;
	}
	public void setLinkRewrite(String linkRewrite) {
		this.linkRewrite = linkRewrite;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public String getCategoryRewrite() {
		return categoryRewrite;
	}
	public void setCategoryRewrite(String categoryRewrite) {
		this.categoryRewrite = categoryRewrite;
	}
	public int getAttributeQuantity() {
		return attributeQuantity;
	}
	public void setAttributeQuantity(int attributeQuantity) {
		this.attributeQuantity = attributeQuantity;
	}
	public String getAttributeSmall() {
		return attributeSmall;
	}
	public void setAttributeSmall(String attributeSmall) {
		this.attributeSmall = attributeSmall;
	}

}
