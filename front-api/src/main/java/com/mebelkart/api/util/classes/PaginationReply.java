/**
 * 
 */
package com.mebelkart.api.util.classes;

/**
 * @author Nikky-Akky
 *
 */
public class PaginationReply {
	private int status;
	private String message;
	private long totalCount;
	private String currentShowing;
	private long currentPage;
	private Object source;
	
	
	public PaginationReply(int status,String message,long totalCount, String currentShowing,long currentPage, Object source) {
		this.status = status;
		this.message = message;
		this.totalCount = totalCount;
		this.currentShowing = currentShowing;
		this.currentPage = currentPage;
		this.source = source;
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
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