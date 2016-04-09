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
	private long totalCount;
	private String currentShowing;
	private long currentPage;
	private Object source;
	
	
	public PaginationReply(int statusCode,String message,long totalCount, String currentShowing,long currentPage, Object source) {
		this.statusCode = statusCode;
		this.message = message;
		this.totalCount = totalCount;
		this.currentShowing = currentShowing;
		this.currentPage = currentPage;
		this.source = source;
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

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
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
