package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderMessage {

	private Integer orderMessageId;
	private Date dateAdd;
	

	public OrderMessage(Integer orderMessageId, Date dateAdd) {
		this.orderMessageId = orderMessageId;
		this.dateAdd = dateAdd;
	}

	public Integer getOrderMessageId() {
		return orderMessageId;
	}

	public void setOrderMessageId(Integer orderMessageId) {
		this.orderMessageId = orderMessageId;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

}
