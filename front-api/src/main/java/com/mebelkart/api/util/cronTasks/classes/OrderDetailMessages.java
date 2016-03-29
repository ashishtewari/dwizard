package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class OrderDetailMessages {
	private Integer messageId;
	private Integer orderDetailId;
	private String orderId;
	private Integer customerId;
	private Integer employeeId;
	private Integer manufacturerId;
	private boolean fromCustomer;
	private boolean fromMaster;
	private boolean fromSeller;
	private boolean toCustomer;
	private boolean toMaster;
	private boolean toSeller;
	private Integer visibleToCustomer;
	private Integer visibleToMaster;
	private Integer visibleToSeller;
	private boolean readByCustomer;
	private boolean readByMaster;
	private boolean readBySeller;
	private String message;
	private Date dateAdd;
	private boolean hasAttachment;
	private boolean isActive;
	

	public OrderDetailMessages(Integer messageId, Integer orderDetailId,
			String orderId, Integer customerId, Integer employeeId,
			Integer manufacturerId, boolean fromCustomer, boolean fromMaster,
			boolean fromSeller, boolean toCustomer, boolean toMaster,
			boolean toSeller, Integer visibleToCustomer,
			Integer visibleToMaster, Integer visibleToSeller,
			boolean readByCustomer, boolean readByMaster, boolean readBySeller,
			String message, Date dateAdd, boolean hasAttachment,
			boolean isActive) {
		this.messageId = messageId;
		this.orderDetailId = orderDetailId;
		this.orderId = orderId;
		this.customerId = customerId;
		this.employeeId = employeeId;
		this.manufacturerId = manufacturerId;
		this.fromCustomer = fromCustomer;
		this.fromMaster = fromMaster;
		this.fromSeller = fromSeller;
		this.toCustomer = toCustomer;
		this.toMaster = toMaster;
		this.toSeller = toSeller;
		this.visibleToCustomer = visibleToCustomer;
		this.visibleToMaster = visibleToMaster;
		this.visibleToSeller = visibleToSeller;
		this.readByCustomer = readByCustomer;
		this.readByMaster = readByMaster;
		this.readBySeller = readBySeller;
		this.message = message;
		this.dateAdd = dateAdd;
		this.hasAttachment = hasAttachment;
		this.isActive = isActive;
	}

	public Integer getMessageId() {
		return messageId;
	}

	public void setMessageId(Integer messageId) {
		this.messageId = messageId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public boolean isFromCustomer() {
		return fromCustomer;
	}

	public void setFromCustomer(boolean fromCustomer) {
		this.fromCustomer = fromCustomer;
	}

	public boolean isFromMaster() {
		return fromMaster;
	}

	public void setFromMaster(boolean fromMaster) {
		this.fromMaster = fromMaster;
	}

	public boolean isFromSeller() {
		return fromSeller;
	}

	public void setFromSeller(boolean fromSeller) {
		this.fromSeller = fromSeller;
	}

	public boolean isToCustomer() {
		return toCustomer;
	}

	public void setToCustomer(boolean toCustomer) {
		this.toCustomer = toCustomer;
	}

	public boolean isToMaster() {
		return toMaster;
	}

	public void setToMaster(boolean toMaster) {
		this.toMaster = toMaster;
	}

	public boolean isToSeller() {
		return toSeller;
	}

	public void setToSeller(boolean toSeller) {
		this.toSeller = toSeller;
	}

	public Integer getVisibleToCustomer() {
		return visibleToCustomer;
	}

	public void setVisibleToCustomer(Integer visibleToCustomer) {
		this.visibleToCustomer = visibleToCustomer;
	}

	public Integer getVisibleToMaster() {
		return visibleToMaster;
	}

	public void setVisibleToMaster(Integer visibleToMaster) {
		this.visibleToMaster = visibleToMaster;
	}

	public Integer getVisibleToSeller() {
		return visibleToSeller;
	}

	public void setVisibleToSeller(Integer visibleToSeller) {
		this.visibleToSeller = visibleToSeller;
	}

	public boolean isReadByCustomer() {
		return readByCustomer;
	}

	public void setReadByCustomer(boolean readByCustomer) {
		this.readByCustomer = readByCustomer;
	}

	public boolean isReadByMaster() {
		return readByMaster;
	}

	public void setReadByMaster(boolean readByMaster) {
		this.readByMaster = readByMaster;
	}

	public boolean isReadBySeller() {
		return readBySeller;
	}

	public void setReadBySeller(boolean readBySeller) {
		this.readBySeller = readBySeller;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public boolean isHasAttachment() {
		return hasAttachment;
	}

	public void setHasAttachment(boolean hasAttachment) {
		this.hasAttachment = hasAttachment;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
