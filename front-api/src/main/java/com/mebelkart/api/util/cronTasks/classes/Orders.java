package com.mebelkart.api.util.cronTasks.classes;

import java.util.Date;

public class Orders {
	private Integer orderId;
	private Integer carrierId;
	private Integer langId;
	private Integer customerId;
	private Integer cartId;
	private Integer currencyId;
	private Integer addressDeliveryId;
	private Integer addressInvoiceId;
	private String secureKey;
	private String payment;
	private double conversionRate;
	private String module;
	private boolean recyclable;
	private boolean gift;
	private String giftMessage;
	private String shippingNumber;
	private boolean totalDiscounts;
	private boolean totalPaid;
	private boolean totalPaidReal;
	private boolean totalProducts;
	private boolean totalProductsWt;
	private boolean totalShipping;
	private boolean carrierTaxRate;
	private boolean totalWrapping;
	private Integer invoiceNumber;
	private Integer deliveryNumber;
	private Date invoiceDate;
	private Date deliveryDate;
	private Integer valid;
	private Date dateAdd;
	private Date dateUpd;
	private boolean isMinimumPayment;
	
	

	public Orders(Integer orderId, Integer carrierId, Integer langId,
			Integer customerId, Integer cartId, Integer currencyId,
			Integer addressDeliveryId, Integer addressInvoiceId,
			String secureKey, String payment, double conversionRate,
			String module, boolean recyclable, boolean gift,
			String giftMessage, String shippingNumber, boolean totalDiscounts,
			boolean totalPaid, boolean totalPaidReal, boolean totalProducts,
			boolean totalProductsWt, boolean totalShipping,
			boolean carrierTaxRate, boolean totalWrapping,
			Integer invoiceNumber, Integer deliveryNumber, Date invoiceDate,
			Date deliveryDate, Integer valid, Date dateAdd, Date dateUpd,
			boolean isMinimumPayment) {
		this.orderId = orderId;
		this.carrierId = carrierId;
		this.langId = langId;
		this.customerId = customerId;
		this.cartId = cartId;
		this.currencyId = currencyId;
		this.addressDeliveryId = addressDeliveryId;
		this.addressInvoiceId = addressInvoiceId;
		this.secureKey = secureKey;
		this.payment = payment;
		this.conversionRate = conversionRate;
		this.module = module;
		this.recyclable = recyclable;
		this.gift = gift;
		this.giftMessage = giftMessage;
		this.shippingNumber = shippingNumber;
		this.totalDiscounts = totalDiscounts;
		this.totalPaid = totalPaid;
		this.totalPaidReal = totalPaidReal;
		this.totalProducts = totalProducts;
		this.totalProductsWt = totalProductsWt;
		this.totalShipping = totalShipping;
		this.carrierTaxRate = carrierTaxRate;
		this.totalWrapping = totalWrapping;
		this.invoiceNumber = invoiceNumber;
		this.deliveryNumber = deliveryNumber;
		this.invoiceDate = invoiceDate;
		this.deliveryDate = deliveryDate;
		this.valid = valid;
		this.dateAdd = dateAdd;
		this.dateUpd = dateUpd;
		this.isMinimumPayment = isMinimumPayment;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public Integer getCarrierId() {
		return carrierId;
	}

	public void setCarrierId(Integer carrierId) {
		this.carrierId = carrierId;
	}

	public Integer getLangId() {
		return langId;
	}

	public void setLangId(Integer langId) {
		this.langId = langId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public Integer getCartId() {
		return cartId;
	}

	public void setCartId(Integer cartId) {
		this.cartId = cartId;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public Integer getAddressDeliveryId() {
		return addressDeliveryId;
	}

	public void setAddressDeliveryId(Integer addressDeliveryId) {
		this.addressDeliveryId = addressDeliveryId;
	}

	public Integer getAddressInvoiceId() {
		return addressInvoiceId;
	}

	public void setAddressInvoiceId(Integer addressInvoiceId) {
		this.addressInvoiceId = addressInvoiceId;
	}

	public String getSecureKey() {
		return secureKey;
	}

	public void setSecureKey(String secureKey) {
		this.secureKey = secureKey;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public double getConversionRate() {
		return conversionRate;
	}

	public void setConversionRate(double conversionRate) {
		this.conversionRate = conversionRate;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public boolean isRecyclable() {
		return recyclable;
	}

	public void setRecyclable(boolean recyclable) {
		this.recyclable = recyclable;
	}

	public boolean isGift() {
		return gift;
	}

	public void setGift(boolean gift) {
		this.gift = gift;
	}

	public String getGiftMessage() {
		return giftMessage;
	}

	public void setGiftMessage(String giftMessage) {
		this.giftMessage = giftMessage;
	}

	public String getShippingNumber() {
		return shippingNumber;
	}

	public void setShippingNumber(String shippingNumber) {
		this.shippingNumber = shippingNumber;
	}

	public boolean isTotalDiscounts() {
		return totalDiscounts;
	}

	public void setTotalDiscounts(boolean totalDiscounts) {
		this.totalDiscounts = totalDiscounts;
	}

	public boolean isTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(boolean totalPaid) {
		this.totalPaid = totalPaid;
	}

	public boolean isTotalPaidReal() {
		return totalPaidReal;
	}

	public void setTotalPaidReal(boolean totalPaidReal) {
		this.totalPaidReal = totalPaidReal;
	}

	public boolean isTotalProducts() {
		return totalProducts;
	}

	public void setTotalProducts(boolean totalProducts) {
		this.totalProducts = totalProducts;
	}

	public boolean isTotalProductsWt() {
		return totalProductsWt;
	}

	public void setTotalProductsWt(boolean totalProductsWt) {
		this.totalProductsWt = totalProductsWt;
	}

	public boolean isTotalShipping() {
		return totalShipping;
	}

	public void setTotalShipping(boolean totalShipping) {
		this.totalShipping = totalShipping;
	}

	public boolean isCarrierTaxRate() {
		return carrierTaxRate;
	}

	public void setCarrierTaxRate(boolean carrierTaxRate) {
		this.carrierTaxRate = carrierTaxRate;
	}

	public boolean isTotalWrapping() {
		return totalWrapping;
	}

	public void setTotalWrapping(boolean totalWrapping) {
		this.totalWrapping = totalWrapping;
	}

	public Integer getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(Integer invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public Integer getDeliveryNumber() {
		return deliveryNumber;
	}

	public void setDeliveryNumber(Integer deliveryNumber) {
		this.deliveryNumber = deliveryNumber;
	}

	public Date getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Integer getValid() {
		return valid;
	}

	public void setValid(Integer valid) {
		this.valid = valid;
	}

	public Date getDateAdd() {
		return dateAdd;
	}

	public void setDateAdd(Date dateAdd) {
		this.dateAdd = dateAdd;
	}

	public Date getDateUpd() {
		return dateUpd;
	}

	public void setDateUpd(Date dateUpd) {
		this.dateUpd = dateUpd;
	}

	public boolean isMinimumPayment() {
		return isMinimumPayment;
	}

	public void setMinimumPayment(boolean isMinimumPayment) {
		this.isMinimumPayment = isMinimumPayment;
	}

}
