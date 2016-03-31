package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderDetailStatusInfo {

	private Integer statusInfoId;
	private Integer orderDetailId;
	private Integer statusId;
	private String statusName;
	private String statusComment;
	private Date statusSetTime;
	private boolean visibleToVendor;
	private boolean visibleToCustomer;
	private boolean active;
	private Integer updatedBy;
	

	public OrderDetailStatusInfo(Integer statusInfoId, Integer orderDetailId,
			Integer statusId, String statusName, String statusComment,
			Date statusSetTime, boolean visibleToVendor,
			boolean visibleToCustomer, boolean active, Integer updatedBy) {
		this.statusInfoId = statusInfoId;
		this.orderDetailId = orderDetailId;
		this.statusId = statusId;
		this.statusName = statusName;
		this.statusComment = statusComment;
		this.statusSetTime = statusSetTime;
		this.visibleToVendor = visibleToVendor;
		this.visibleToCustomer = visibleToCustomer;
		this.active = active;
		this.updatedBy = updatedBy;
	}

	public Integer getStatusInfoId() {
		return statusInfoId;
	}

	public void setStatusInfoId(Integer statusInfoId) {
		this.statusInfoId = statusInfoId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getStatusId() {
		return statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getStatusComment() {
		return statusComment;
	}

	public void setStatusComment(String statusComment) {
		this.statusComment = statusComment;
	}

	public Date getStatusSetTime() {
		return statusSetTime;
	}

	public void setStatusSetTime(Date statusSetTime) {
		this.statusSetTime = statusSetTime;
	}

	public boolean isVisibleToVendor() {
		return visibleToVendor;
	}

	public void setVisibleToVendor(boolean visibleToVendor) {
		this.visibleToVendor = visibleToVendor;
	}

	public boolean isVisibleToCustomer() {
		return visibleToCustomer;
	}

	public void setVisibleToCustomer(boolean visibleToCustomer) {
		this.visibleToCustomer = visibleToCustomer;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

}
