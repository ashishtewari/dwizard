package com.mebelkart.api.util.cronTasks.classes;

/**
 * Created by vinitpayal on 28/03/16.
 */
public class OrderDetailLogisticService {

    private Integer idOrderDetailLogisticService;
    private Integer idOrderDetail;
    private String idOrder;
    private Integer logisticService;
    private Integer logisticServiceMode;
    private Integer noOfPackages;
    private String shipmentWeight;
    private String shipmentWeightUnit;
    private String dimensions;
    private String dimensionUnit;
    private String remarks;
    private String currentPickupDate;
    private String addressCloseTime;
    private String invoiceValue;
    private boolean carrierRiskInfo;
    private boolean active;
    private Integer useAmb;
    private boolean migratedToGetit;


    public OrderDetailLogisticService(Integer idOrderDetailLogisticService, Integer idOrderDetail, String idOrder
            , Integer logisticService, Integer logisticServiceMode, Integer noOfPackages, String shipmentWeight
            , String shipmentWeightUnit, String dimensions, String dimensionUnit, String remarks, String currentPickupDate
            , String addressCloseTime, String invoiceValue, boolean carrierRiskInfo, boolean active, Integer useAmb
            , boolean migratedToGetit) {
        this.idOrderDetailLogisticService = idOrderDetailLogisticService;
        this.idOrderDetail = idOrderDetail;
        this.idOrder = idOrder;
        this.logisticService = logisticService;
        this.logisticServiceMode = logisticServiceMode;
        this.noOfPackages = noOfPackages;
        this.shipmentWeight = shipmentWeight;
        this.shipmentWeightUnit = shipmentWeightUnit;
        this.dimensions = dimensions;
        this.dimensionUnit = dimensionUnit;
        this.remarks = remarks;
        this.currentPickupDate = currentPickupDate;
        this.addressCloseTime = addressCloseTime;
        this.invoiceValue = invoiceValue;
        this.carrierRiskInfo = carrierRiskInfo;
        this.active = active;
        this.useAmb = useAmb;
        this.migratedToGetit = migratedToGetit;
    }

    public Integer getIdOrderDetailLogisticService() {
        return idOrderDetailLogisticService;
    }

    public void setIdOrderDetailLogisticService(Integer idOrderDetailLogisticService) {
        this.idOrderDetailLogisticService = idOrderDetailLogisticService;
    }

    public Integer getIdOrderDetail() {
        return idOrderDetail;
    }

    public void setIdOrderDetail(Integer idOrderDetail) {
        this.idOrderDetail = idOrderDetail;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
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

    public String getCurrentPickupDate() {
        return currentPickupDate;
    }

    public void setCurrentPickupDate(String currentPickupDate) {
        this.currentPickupDate = currentPickupDate;
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getUseAmb() {
        return useAmb;
    }

    public void setUseAmb(Integer useAmb) {
        this.useAmb = useAmb;
    }

    public boolean isMigratedToGetit() {
        return migratedToGetit;
    }

    public void setMigratedToGetit(boolean migratedToGetit) {
        this.migratedToGetit = migratedToGetit;
    }
}

