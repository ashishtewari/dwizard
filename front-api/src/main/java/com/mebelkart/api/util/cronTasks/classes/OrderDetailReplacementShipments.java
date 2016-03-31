package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailReplacementShipments {
	private Integer shipmentId;
	private Integer orderDetailId;
	private String shipmentInformation;
	private Integer logisticServiceId;
	private Integer logisticServiceModeId;
	private String carrierName;
	private String carrierTrackingId;
	private boolean isOtherLogisticService;
	private boolean isDefault;
	private boolean isActive;
	

	public OrderDetailReplacementShipments(Integer shipmentId,
			Integer orderDetailId, String shipmentInformation,
			Integer logisticServiceId, Integer logisticServiceModeId,
			String carrierName, String carrierTrackingId,
			boolean isOtherLogisticService, boolean isDefault, boolean isActive) {
		this.shipmentId = shipmentId;
		this.orderDetailId = orderDetailId;
		this.shipmentInformation = shipmentInformation;
		this.logisticServiceId = logisticServiceId;
		this.logisticServiceModeId = logisticServiceModeId;
		this.carrierName = carrierName;
		this.carrierTrackingId = carrierTrackingId;
		this.isOtherLogisticService = isOtherLogisticService;
		this.isDefault = isDefault;
		this.isActive = isActive;
	}

	public Integer getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(Integer shipmentId) {
		this.shipmentId = shipmentId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public String getShipmentInformation() {
		return shipmentInformation;
	}

	public void setShipmentInformation(String shipmentInformation) {
		this.shipmentInformation = shipmentInformation;
	}

	public Integer getLogisticServiceId() {
		return logisticServiceId;
	}

	public void setLogisticServiceId(Integer logisticServiceId) {
		this.logisticServiceId = logisticServiceId;
	}

	public Integer getLogisticServiceModeId() {
		return logisticServiceModeId;
	}

	public void setLogisticServiceModeId(Integer logisticServiceModeId) {
		this.logisticServiceModeId = logisticServiceModeId;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getCarrierTrackingId() {
		return carrierTrackingId;
	}

	public void setCarrierTrackingId(String carrierTrackingId) {
		this.carrierTrackingId = carrierTrackingId;
	}

	public boolean isOtherLogisticService() {
		return isOtherLogisticService;
	}

	public void setOtherLogisticService(boolean isOtherLogisticService) {
		this.isOtherLogisticService = isOtherLogisticService;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
}
