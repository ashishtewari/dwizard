/**
 * 
 */
package com.mebelkart.api.mobile.v1.core;

import java.util.List;

/**
 * @author Tinku
 *
 */
public class MobileDealsWrapper {
	private String type;
	private String category_id;
	private String category_name;
	private List<ProductDetailsWrapper> children;
	
	public MobileDealsWrapper(){
		
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getCategory_id() {
		return category_id;
	}
	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}
	public String getCategory_name() {
		return category_name;
	}
	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}
	public List<ProductDetailsWrapper> getChildren() {
		return children;
	}
	public void setChildren(List<ProductDetailsWrapper> children) {
		this.children = children;
	}
}
