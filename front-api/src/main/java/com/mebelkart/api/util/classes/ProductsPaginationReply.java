package com.mebelkart.api.util.classes;

public class ProductsPaginationReply {
	private int status;
	private String message;
	private long totalProducts;
	private long totalPages;
	private int currentPage;
	private String currentShowing;
	private Object source;

	public ProductsPaginationReply(int status,String message,long totalProducts,long totalPages,int currentPage,String currentShowing,Object source) {
		this.setStatus(status);
		this.setMessage(message);
		this.setTotalProducts(totalProducts);
		this.setTotalPages(totalPages);
		this.setCurrentShowing(currentShowing);
		this.setCurrentPage(currentPage);
		this.setSource(source);
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

	public long getTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(long totalProducts) {
		this.totalProducts = totalProducts;
	}

	public long getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getCurrentShowing() {
		return currentShowing;
	}

	public void setCurrentShowing(String currentShowing) {
		this.currentShowing = currentShowing;
	}

	public Object getSource() {
		return source;
	}

	public void setSource(Object source) {
		this.source = source;
	}

}
