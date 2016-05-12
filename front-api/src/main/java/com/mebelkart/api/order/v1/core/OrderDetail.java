package com.mebelkart.api.order.v1.core;

/**
 * Created by vinitpayal on 21/04/16.
 */
public class OrderDetail {

    private Integer orderDetailId;
    private Integer orderId;
    private Integer productId;
    private Integer productAttributeId;
    private String productName;
    private Integer productQuantity;
    private Integer productQuantityInStock;
    private Integer productQuantityRefunded;
    private Integer productQuantityReinjected;
    private Float orderDetailWholeSalePrice;
    private Float productPrice;
    private Float reductionPercentage;
    private Float reductionAmount;
    private Float groupReduction;
    private Float productQuantityDiscount;
    private Float orderDetailProductAdvanceAmount;
    private Integer shippedFromDate;
    private Integer shippedToDate;
    private Integer deliveredFromDate;
    private Integer deliveredToDate;
    private String currentOrderStatus;
    private Integer currentOrderStatusId;

    public Integer getCurrentOrderStatusId() {
        return currentOrderStatusId;
    }

    public void setCurrentOrderStatusId(Integer currentOrderStatusId) {
        this.currentOrderStatusId = currentOrderStatusId;
    }

    public String getCurrentOrderStatus() {
        return currentOrderStatus;
    }

    public void setCurrentOrderStatus(String currentOrderStatus) {
        this.currentOrderStatus = currentOrderStatus;
    }

    private String productEan13;
    private String productUpc;
    private String productRefrence;
    private String productSupplierRefrence;
    private Float productWeight;
    private String taxName;
    private Float taxRate;
    private Float ecoTax;
    private Float ecotaxRate;
    private Boolean discountQuantityApplied;
    private String downloadHash;
    private Integer downloadNb;
    private String downloadDeadline;

    public OrderDetail(Integer orderDetailId, Integer orderId, Integer productId, Integer productAttributeId
            , String productName, Integer productQuantity, Integer productQuantityInStock, Integer productQuantityRefunded
            , Integer productQuantityReinjected, Float orderDetailWholeSalePrice, Float productPrice, Float reductionPercentage
            , Float reductionAmount, Float groupReduction, Float productQuantityDiscount, Float orderDetailProductAdvanceAmount
            , Integer shippedFromDate, Integer shippedToDate, Integer deliveredFromDate, Integer deliveredToDate, String productEan13
            , String productUpc, String productRefrence, String productSupplierRefrence, Float productWeight, String taxName
            , Float taxRate, Float ecoTax, Float ecotaxRate, Boolean discountQuantityApplied, String downloadHash, Integer downloadNb
            , String downloadDeadline,String currentOrderStatus,Integer currentOrderStatusId) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.productAttributeId = productAttributeId;
        this.productName = productName;
        this.productQuantity = productQuantity;
        this.productQuantityInStock = productQuantityInStock;
        this.productQuantityRefunded = productQuantityRefunded;
        this.productQuantityReinjected = productQuantityReinjected;
        this.orderDetailWholeSalePrice = orderDetailWholeSalePrice;
        this.productPrice = productPrice;
        this.reductionPercentage = reductionPercentage;
        this.reductionAmount = reductionAmount;
        this.groupReduction = groupReduction;
        this.productQuantityDiscount = productQuantityDiscount;
        this.orderDetailProductAdvanceAmount = orderDetailProductAdvanceAmount;
        this.shippedFromDate = shippedFromDate;
        this.shippedToDate = shippedToDate;
        this.deliveredFromDate = deliveredFromDate;
        this.deliveredToDate = deliveredToDate;
        this.productEan13 = productEan13;
        this.productUpc = productUpc;
        this.productRefrence = productRefrence;
        this.productSupplierRefrence = productSupplierRefrence;
        this.productWeight = productWeight;
        this.taxName = taxName;
        this.taxRate = taxRate;
        this.ecoTax = ecoTax;
        this.ecotaxRate = ecotaxRate;
        this.discountQuantityApplied = discountQuantityApplied;
        this.downloadHash = downloadHash;
        this.downloadNb = downloadNb;
        this.downloadDeadline = downloadDeadline;
        this.currentOrderStatus=currentOrderStatus;
        this.currentOrderStatusId=currentOrderStatusId;
    }

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductAttributeId() {
        return productAttributeId;
    }

    public void setProductAttributeId(Integer productAttributeId) {
        this.productAttributeId = productAttributeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Integer getProductQuantityInStock() {
        return productQuantityInStock;
    }

    public void setProductQuantityInStock(Integer productQuantityInStock) {
        this.productQuantityInStock = productQuantityInStock;
    }

    public Integer getProductQuantityRefunded() {
        return productQuantityRefunded;
    }

    public void setProductQuantityRefunded(Integer productQuantityRefunded) {
        this.productQuantityRefunded = productQuantityRefunded;
    }

    public Integer getProductQuantityReinjected() {
        return productQuantityReinjected;
    }

    public void setProductQuantityReinjected(Integer productQuantityReinjected) {
        this.productQuantityReinjected = productQuantityReinjected;
    }

    public Float getOrderDetailWholeSalePrice() {
        return orderDetailWholeSalePrice;
    }

    public void setOrderDetailWholeSalePrice(Float orderDetailWholeSalePrice) {
        this.orderDetailWholeSalePrice = orderDetailWholeSalePrice;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    public Float getReductionPercentage() {
        return reductionPercentage;
    }

    public void setReductionPercentage(Float reductionPercentage) {
        this.reductionPercentage = reductionPercentage;
    }

    public Float getReductionAmount() {
        return reductionAmount;
    }

    public void setReductionAmount(Float reductionAmount) {
        this.reductionAmount = reductionAmount;
    }

    public Float getGroupReduction() {
        return groupReduction;
    }

    public void setGroupReduction(Float groupReduction) {
        this.groupReduction = groupReduction;
    }

    public Float getProductQuantityDiscount() {
        return productQuantityDiscount;
    }

    public void setProductQuantityDiscount(Float productQuantityDiscount) {
        this.productQuantityDiscount = productQuantityDiscount;
    }

    public Float getOrderDetailProductAdvanceAmount() {
        return orderDetailProductAdvanceAmount;
    }

    public void setOrderDetailProductAdvanceAmount(Float orderDetailProductAdvanceAmount) {
        this.orderDetailProductAdvanceAmount = orderDetailProductAdvanceAmount;
    }

    public Integer getShippedFromDate() {
        return shippedFromDate;
    }

    public void setShippedFromDate(Integer shippedFromDate) {
        this.shippedFromDate = shippedFromDate;
    }

    public Integer getShippedToDate() {
        return shippedToDate;
    }

    public void setShippedToDate(Integer shippedToDate) {
        this.shippedToDate = shippedToDate;
    }

    public Integer getDeliveredFromDate() {
        return deliveredFromDate;
    }

    public void setDeliveredFromDate(Integer deliveredFromDate) {
        this.deliveredFromDate = deliveredFromDate;
    }

    public Integer getDeliveredToDate() {
        return deliveredToDate;
    }

    public void setDeliveredToDate(Integer deliveredToDate) {
        this.deliveredToDate = deliveredToDate;
    }

    public String getProductEan13() {
        return productEan13;
    }

    public void setProductEan13(String productEan13) {
        this.productEan13 = productEan13;
    }

    public String getProductUpc() {
        return productUpc;
    }

    public void setProductUpc(String productUpc) {
        this.productUpc = productUpc;
    }

    public String getProductRefrence() {
        return productRefrence;
    }

    public void setProductRefrence(String productRefrence) {
        this.productRefrence = productRefrence;
    }

    public String getProductSupplierRefrence() {
        return productSupplierRefrence;
    }

    public void setProductSupplierRefrence(String productSupplierRefrence) {
        this.productSupplierRefrence = productSupplierRefrence;
    }

    public Float getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(Float productWeight) {
        this.productWeight = productWeight;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public Float getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Float taxRate) {
        this.taxRate = taxRate;
    }

    public Float getEcoTax() {
        return ecoTax;
    }

    public void setEcoTax(Float ecoTax) {
        this.ecoTax = ecoTax;
    }

    public Float getEcotaxRate() {
        return ecotaxRate;
    }

    public void setEcotaxRate(Float ecotaxRate) {
        this.ecotaxRate = ecotaxRate;
    }

    public Boolean getDiscountQuantityApplied() {
        return discountQuantityApplied;
    }

    public void setDiscountQuantityApplied(Boolean discountQuantityApplied) {
        this.discountQuantityApplied = discountQuantityApplied;
    }

    public String getDownloadHash() {
        return downloadHash;
    }

    public void setDownloadHash(String downloadHash) {
        this.downloadHash = downloadHash;
    }

    public Integer getDownloadNb() {
        return downloadNb;
    }

    public void setDownloadNb(Integer downloadNb) {
        this.downloadNb = downloadNb;
    }

    public String getDownloadDeadline() {
        return downloadDeadline;
    }

    public void setDownloadDeadline(String downloadDeadline) {
        this.downloadDeadline = downloadDeadline;
    }
}