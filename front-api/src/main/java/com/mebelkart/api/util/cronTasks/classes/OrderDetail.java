package com.mebelkart.api.util.cronTasks.classes;

/**
 * Created by vinitpayal on 28/03/16.
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
    private Integer orderDetailWholeSalePrice;
    private Integer productPrice;
    private Integer reductionPercentage;
    private Integer reductionAmount;
    private Integer groupReduction;
    private Integer productQuantityDiscount;
    private Integer orderDetailProductAdvanceAmount;
    private Integer shippedFromDate;
    private Integer shippedToDate;
    private Integer deliveredFromDate;
    private Integer deliveredToDate;
    private String productEan13;
    private String productUpc;

    private String productRefrence;
    private String productSupplierRefrence;
    private String productWeight;
    private String taxName;
    private Float taxRate;
    private Float EcotaxRate;
    private Float discountQuantityApplied;
    private Float downloadHash;
    private Float downloadNb;
    private Float downloadDeadline;

    public OrderDetail(Integer orderDetailId, Integer orderId, Integer productId
            ,Integer productAttributeId, String productName, Integer productQuantity
            ,Integer productQuantityInStock, Integer productQuantityRefunded
            , Integer productQuantityReinjected, Integer orderDetailWholeSalePrice
            , Integer productPrice, Integer reductionPercentage, Integer reductionAmount
            , Integer groupReduction, Integer productQuantityDiscount
            , Integer orderDetailProductAdvanceAmount, Integer shippedFromDate, Integer shippedToDate
            , Integer deliveredFromDate, Integer deliveredToDate, String productEan13, String productUpc
            , String productRefrence, String productSupplierRefrence, String productWeight, String taxName
            , Float taxRate, Float ecotaxRate, Float discountQuantityApplied, Float downloadHash, Float downloadNb
            , Float downloadDeadline) {
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
        EcotaxRate = ecotaxRate;
        this.discountQuantityApplied = discountQuantityApplied;
        this.downloadHash = downloadHash;
        this.downloadNb = downloadNb;
        this.downloadDeadline = downloadDeadline;
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

    public Integer getOrderDetailWholeSalePrice() {
        return orderDetailWholeSalePrice;
    }

    public void setOrderDetailWholeSalePrice(Integer orderDetailWholeSalePrice) {
        this.orderDetailWholeSalePrice = orderDetailWholeSalePrice;
    }

    public Integer getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    public Integer getReductionPercentage() {
        return reductionPercentage;
    }

    public void setReductionPercentage(Integer reductionPercentage) {
        this.reductionPercentage = reductionPercentage;
    }

    public Integer getReductionAmount() {
        return reductionAmount;
    }

    public void setReductionAmount(Integer reductionAmount) {
        this.reductionAmount = reductionAmount;
    }

    public Integer getGroupReduction() {
        return groupReduction;
    }

    public void setGroupReduction(Integer groupReduction) {
        this.groupReduction = groupReduction;
    }

    public Integer getProductQuantityDiscount() {
        return productQuantityDiscount;
    }

    public void setProductQuantityDiscount(Integer productQuantityDiscount) {
        this.productQuantityDiscount = productQuantityDiscount;
    }

    public Integer getOrderDetailProductAdvanceAmount() {
        return orderDetailProductAdvanceAmount;
    }

    public void setOrderDetailProductAdvanceAmount(Integer orderDetailProductAdvanceAmount) {
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

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
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

    public Float getEcotaxRate() {
        return EcotaxRate;
    }

    public void setEcotaxRate(Float ecotaxRate) {
        EcotaxRate = ecotaxRate;
    }

    public Float getDiscountQuantityApplied() {
        return discountQuantityApplied;
    }

    public void setDiscountQuantityApplied(Float discountQuantityApplied) {
        this.discountQuantityApplied = discountQuantityApplied;
    }

    public Float getDownloadHash() {
        return downloadHash;
    }

    public void setDownloadHash(Float downloadHash) {
        this.downloadHash = downloadHash;
    }

    public Float getDownloadNb() {
        return downloadNb;
    }

    public void setDownloadNb(Float downloadNb) {
        this.downloadNb = downloadNb;
    }

    public Float getDownloadDeadline() {
        return downloadDeadline;
    }

    public void setDownloadDeadline(Float downloadDeadline) {
        this.downloadDeadline = downloadDeadline;
    }
}
