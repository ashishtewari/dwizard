/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikky-Akky
 *
 */
@JsonInclude(Include.NON_NULL)
public class CustomerWishListWrapper {

	@PrimaryKey()
	@ColumnName("id_wishlist")
	private int wishListId;
	@ColumnName("name")
	private String wishListName;
	@ColumnName("date_add")
	private String addDate;
	@ColumnName("date_upd")
	private String updateDate;

	public int getWishListId() {
		return wishListId;
	}

	public void setWishListId(int wishListId) {
		this.wishListId = wishListId;
	}

	public String getWishListName() {
		return wishListName;
	}

	public void setWishListName(String wishListName) {
		this.wishListName = wishListName;
	}

	public String getAddDate() {
		return addDate;
	}

	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

}
