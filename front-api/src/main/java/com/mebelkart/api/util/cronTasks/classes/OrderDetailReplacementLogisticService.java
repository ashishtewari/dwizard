package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailReplacementLogisticService {
	private Integer orderDetailLogisticServiceId;
	private Integer orderDetailId;
	private String orderId;
	private Integer logisticService;
	private Integer logisticServiceMode;
	private Integer noOfPackages;
	private String shipmentWeight;
	private String shipmentWeightUnit;
	private String dimensions;
	private String dimensionUnit;
	private String remarks;
	private String currentPickUpDate;
	private String addressCloseTime;
	private String invoiceValue;
	private boolean carrierRiskInfo;
	private boolean isActive;
	

	public OrderDetailReplacementLogisticService(
			Integer orderDetailLogisticServiceId, Integer orderDetailId,
			String orderId, Integer logisticService,
			Integer logisticServiceMode, Integer noOfPackages,
			String shipmentWeight, String shipmentWeightUnit,
			String dimensions, String dimensionUnit, String remarks,
			String currentPickUpDate, String addressCloseTime,
			String invoiceValue, boolean carrierRiskInfo, boolean isActive) {
		this.orderDetailLogisticServiceId = orderDetailLogisticServiceId;
		this.orderDetailId = orderDetailId;
		this.orderId = orderId;
		this.logisticService = logisticService;
		this.logisticServiceMode = logisticServiceMode;
		this.noOfPackages = noOfPackages;
		this.shipmentWeight = shipmentWeight;
		this.shipmentWeightUnit = shipmentWeightUnit;
		this.dimensions = dimensions;
		this.dimensionUnit = dimensionUnit;
		this.remarks = remarks;
		this.currentPickUpDate = currentPickUpDate;
		this.addressCloseTime = addressCloseTime;
		this.invoiceValue = invoiceValue;
		this.carrierRiskInfo = carrierRiskInfo;
		this.isActive = isActive;
	}

	public Integer getOrderDetailLogisticServiceId() {
		return orderDetailLogisticServiceId;
	}

	public void setOrderDetailLogisticServiceId(
			Integer orderDetailLogisticServiceId) {
		this.orderDetailLogisticServiceId = orderDetailLogisticServiceId;
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

	public Integer getLogisticService() {
		return logisticService;
	}

	public void setLogisticService(Integer logisticService) {
		this.logisticService = logisticService;
	}

	public Integer getLogisticServiceMode() {
		return logisticServiceMode;
	}

	public void setLogisticServiceMode(Integer logisticServiceMode) {
		this.logisticServiceMode = logisticServiceMode;
	}

	public Integer getNoOfPackages() {
		return noOfPackages;
	}

	public void setNoOfPackages(Integer noOfPackages) {
		this.noOfPackages = noOfPackages;
	}

	public String getShipmentWeight() {
		return shipmentWeight;
	}

	public void setShipmentWeight(String shipmentWeight) {
		this.shipmentWeight = shipmentWeight;
	}

	public String getShipmentWeightUnit() {
		return shipmentWeightUnit;
	}

	public void setShipmentWeightUnit(String shipmentWeightUnit) {
		this.shipmentWeightUnit = shipmentWeightUnit;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public String getDimensionUnit() {
		return dimensionUnit;
	}

	public void setDimensionUnit(String dimensionUnit) {
		this.dimensionUnit = dimensionUnit;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getCurrentPickUpDate() {
		return currentPickUpDate;
	}

	public void setCurrentPickUpDate(String currentPickUpDate) {
		this.currentPickUpDate = currentPickUpDate;
	}

	public String getAddressCloseTime() {
		return addressCloseTime;
	}

	public void setAddressCloseTime(String addressCloseTime) {
		this.addressCloseTime = addressCloseTime;
	}

	public String getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue) {
		this.invoiceValue = invoiceValue;
	}

	public boolean isCarrierRiskInfo() {
		return carrierRiskInfo;
	}

	public void setCarrierRiskInfo(boolean carrierRiskInfo) {
		this.carrierRiskInfo = carrierRiskInfo;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
