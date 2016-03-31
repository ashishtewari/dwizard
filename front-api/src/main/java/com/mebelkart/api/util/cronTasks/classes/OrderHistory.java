package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderHistory {

	private Integer orderHistoryId;
	private Integer employeeId;
	private Integer orderId;
	private Integer orderStateId;
	private Date dateAdd;

	public OrderHistory(Integer orderHistoryId, Integer employeeId,Integer orderId, Integer orderStateId, Date dateAdd) {
		this.orderHistoryId = orderHistoryId;
		this.employeeId = employeeId;
		this.orderId = orderId;
		this.orderStateId = orderStateId;
		this.dateAdd = dateAdd;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getOrderStateId() {
		return orderStateId;
	}

	public void setOrderStateId(Integer orderStateId) {
		this.orderStateId = orderStateId;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public Integer getOrderHistoryId() {
		return orderHistoryId;
	}

	public void setOrderHistoryId(Integer orderHistoryId) {
		this.orderHistoryId = orderHistoryId;
	}

}
