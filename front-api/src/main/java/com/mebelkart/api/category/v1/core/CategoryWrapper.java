/**
 * 
 */
package com.mebelkart.api.category.v1.core;

import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikhil
 *
 */
public class CategoryWrapper {
	@PrimaryKey
	@ColumnName("id_category")
	private int categoryId;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	

}
