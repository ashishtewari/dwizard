package com.mebelkart.api.util.cronTasks.classes;

public class OrderDetailPaymentInfo {
	private Integer transactionId;
	private Integer orderDetailId;
	private Integer manufacturerId;
	private float productPriceToCustomer;
	private float productPriceToSeller;
	private float amountCollectedBySeller;
	private float amountCollectedByMebelkart;
	private float advancePaidTOSeller;
	private String transactionDate;
	private String transactionMode;
	private String transactionBankReference;
	private float vendorSuggestedTransferPrice;
	private float otherCharges;
	private String transactionComments;
	private float totalAmountTransferred;
	private boolean isPaymentDone;
	

	public OrderDetailPaymentInfo(Integer transactionId, Integer orderDetailId,
			Integer manufacturerId, float productPriceToCustomer,
			float productPriceToSeller, float amountCollectedBySeller,
			float amountCollectedByMebelkart, float advancePaidTOSeller,
			String transactionDate, String transactionMode,
			String transactionBankReference,
			float vendorSuggestedTransferPrice, float otherCharges,
			String transactionComments, float totalAmountTransferred,
			boolean isPaymentDone) {
		this.transactionId = transactionId;
		this.orderDetailId = orderDetailId;
		this.manufacturerId = manufacturerId;
		this.productPriceToCustomer = productPriceToCustomer;
		this.productPriceToSeller = productPriceToSeller;
		this.amountCollectedBySeller = amountCollectedBySeller;
		this.amountCollectedByMebelkart = amountCollectedByMebelkart;
		this.advancePaidTOSeller = advancePaidTOSeller;
		this.transactionDate = transactionDate;
		this.transactionMode = transactionMode;
		this.transactionBankReference = transactionBankReference;
		this.vendorSuggestedTransferPrice = vendorSuggestedTransferPrice;
		this.otherCharges = otherCharges;
		this.transactionComments = transactionComments;
		this.totalAmountTransferred = totalAmountTransferred;
		this.isPaymentDone = isPaymentDone;
	}

	public Integer getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public Integer getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(Integer orderDetailId) {
		this.orderDetailId = orderDetailId;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public float getProductPriceToCustomer() {
		return productPriceToCustomer;
	}

	public void setProductPriceToCustomer(float productPriceToCustomer) {
		this.productPriceToCustomer = productPriceToCustomer;
	}

	public float getProductPriceToSeller() {
		return productPriceToSeller;
	}

	public void setProductPriceToSeller(float productPriceToSeller) {
		this.productPriceToSeller = productPriceToSeller;
	}

	public float getAmountCollectedBySeller() {
		return amountCollectedBySeller;
	}

	public void setAmountCollectedBySeller(float amountCollectedBySeller) {
		this.amountCollectedBySeller = amountCollectedBySeller;
	}

	public float getAmountCollectedByMebelkart() {
		return amountCollectedByMebelkart;
	}

	public void setAmountCollectedByMebelkart(float amountCollectedByMebelkart) {
		this.amountCollectedByMebelkart = amountCollectedByMebelkart;
	}

	public float getAdvancePaidTOSeller() {
		return advancePaidTOSeller;
	}

	public void setAdvancePaidTOSeller(float advancePaidTOSeller) {
		this.advancePaidTOSeller = advancePaidTOSeller;
	}

	public String getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getTransactionMode() {
		return transactionMode;
	}

	public void setTransactionMode(String transactionMode) {
		this.transactionMode = transactionMode;
	}

	public String getTransactionBankReference() {
		return transactionBankReference;
	}

	public void setTransactionBankReference(String transactionBankReference) {
		this.transactionBankReference = transactionBankReference;
	}

	public float getVendorSuggestedTransferPrice() {
		return vendorSuggestedTransferPrice;
	}

	public void setVendorSuggestedTransferPrice(
			float vendorSuggestedTransferPrice) {
		this.vendorSuggestedTransferPrice = vendorSuggestedTransferPrice;
	}

	public float getOtherCharges() {
		return otherCharges;
	}

	public void setOtherCharges(float otherCharges) {
		this.otherCharges = otherCharges;
	}

	public String getTransactionComments() {
		return transactionComments;
	}

	public void setTransactionComments(String transactionComments) {
		this.transactionComments = transactionComments;
	}

	public float getTotalAmountTransferred() {
		return totalAmountTransferred;
	}

	public void setTotalAmountTransferred(float totalAmountTransferred) {
		this.totalAmountTransferred = totalAmountTransferred;
	}

	public boolean isPaymentDone() {
		return isPaymentDone;
	}

	public void setPaymentDone(boolean isPaymentDone) {
		this.isPaymentDone = isPaymentDone;
	}

}
