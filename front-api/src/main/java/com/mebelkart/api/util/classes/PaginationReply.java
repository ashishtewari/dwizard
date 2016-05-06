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
	private long totalPages;
	private String currentShowing;
	private long currentPage;
	private Object source;
	
	
	public PaginationReply(int status,String message,long totalCount, long totalPages, long currentPage,String currentShowing, Object source) {
		this.status = status;
		this.message = message;
		this.totalCount = totalCount;
		this.currentShowing = currentShowing;
		this.currentPage = currentPage;
		this.source = source;
		this.totalPages = totalPages;
	}
	
	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
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