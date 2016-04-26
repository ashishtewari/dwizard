/**
 * 
 */
package com.mebelkart.api.util;

/**
 * @author Nikky-Akky
 *
 */
public class PaginationReply {
	private int statusCode;
	private String message;
	private long totalAddresses;
	private long totalProducts;
	private long totalOrders;
	private String currentShowing;
	private long currentPage;
	private Object source;
	
	
	public PaginationReply(int statusCode,String message,long totalAddresses,long totalProducts,long totalOrders, String currentShowing,long currentPage, Object source) {
		this.statusCode = statusCode;
		this.message = message;
		this.totalAddresses = totalAddresses;
		this.totalProducts = totalProducts;
		this.totalOrders = totalOrders;
		this.currentShowing = currentShowing;
		this.currentPage = currentPage;
		this.source = source;
	}
	
	public long getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(long totalProducts) {
		this.totalProducts = totalProducts;
	}

	public long getTotalOrders() {
		return totalOrders;
	}

	public void setTotalOrders(long totalOrders) {
		this.totalOrders = totalOrders;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTotalAddresses() {
		return totalAddresses;
	}

	public void setTotalAddresses(long totalCount) {
		this.totalAddresses = totalCount;
	}

	public String getCurrentShowing() {
		return currentShowing;
	}

	public void setCurrentShowing(String currentShowing) {
		this.currentShowing = currentShowing;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
