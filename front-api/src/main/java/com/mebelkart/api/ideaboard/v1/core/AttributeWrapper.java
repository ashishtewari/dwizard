/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.core;

/**
 * @author Tinku
 *
 */
public class AttributeWrapper {
	
	private String attributeName;
	private int attributeQuantity;
	
	public AttributeWrapper(String attrName, int attrQty){
		this.setAttributeName(attrName);
		this.setAttributeQuantity(attrQty);
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public int getAttributeQuantity() {
		return attributeQuantity;
	}

	public void setAttributeQuantity(int attributeQuantity) {
		this.attributeQuantity = attributeQuantity;
	}

}
