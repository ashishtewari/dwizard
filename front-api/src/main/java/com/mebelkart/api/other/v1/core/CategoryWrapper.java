/**
 * 
 */
package com.mebelkart.api.other.v1.core;

/**
 * @author Tinku
 *
 */
public class CategoryWrapper {
	
	private int catId;
	private String catName;
	
	public CategoryWrapper(int catId, String catName){
		this.setCatId(catId);
		this.setCatName(catName);
	}

	public int getCatId() {
		return catId;
	}

	public void setCatId(int catId) {
		this.catId = catId;
	}

	public String getCatName() {
		return catName;
	}

	public void setCatName(String catName) {
		this.catName = catName;
	}

}
