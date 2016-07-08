/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.core;

/**
 * @author Tinku
 *
 */
public class WishListWrapper {
	
	private int wishListId;
	private String name;
	private String token;
	private String addDate;
	private String updDate;
	private int counter;
	private int totalProducts;
	private int totalNoProducts;
	/**
	 * Default Constructer
	 */
	public WishListWrapper(int wishList,String name,String token,String addDate, String updDate,int counter) {
		this.setWishListId(wishList);
		this.setName(name);
		this.setToken(token);
		this.setAddDate(addDate);
		this.setUpdDate(updDate);
		this.setCounter(counter);
	}
	public int getWishListId() {
		return wishListId;
	}
	public void setWishListId(int wishListId) {
		this.wishListId = wishListId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getAddDate() {
		return addDate;
	}
	public void setAddDate(String addDate) {
		this.addDate = addDate;
	}
	public String getUpdDate() {
		return updDate;
	}
	public void setUpdDate(String updDate) {
		this.updDate = updDate;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public int getTotalProducts() {
		return totalProducts;
	}
	public void setTotalProducts(int totalProducts) {
		this.totalProducts = totalProducts;
	}
	public int getTotalNoProducts() {
		return totalNoProducts;
	}
	public void setTotalNoProducts(int totalNoProducts) {
		this.totalNoProducts = totalNoProducts;
	}

}
