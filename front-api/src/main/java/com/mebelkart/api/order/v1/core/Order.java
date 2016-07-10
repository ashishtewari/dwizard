package com.mebelkart.api.order.v1.core;

import java.util.List;

/**
 * Created by vinitpayal on 20/04/16.
 */
public class Order {
    private Integer orderId;
    private Integer customerId;
    private Integer cartId;
    private Integer currencyId;
    private String paymentMode;
    private String module;
    private Integer idAddressDelivery;
    private Integer idAddressInvoice;
    private List<OrderDetail> orderDetails;
    private Address addressDelivery;
    private Address addressInvoice;
    private Customer customer;

    public Order(Integer orderId, Integer customerId, Integer cartId, Integer currencyId, String paymentMode, String module
            , Integer idAddressDelivery, Integer idAddressInvoice,Customer customer,Address addressDelivery,Address addressInvoice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.cartId = cartId;
        this.currencyId = currencyId;
        this.paymentMode = paymentMode;
        this.module = module;
        this.setIdAddressDelivery(idAddressDelivery);
        this.setIdAddressInvoice(idAddressInvoice);
        this.customer=customer;
        this.addressDelivery=addressDelivery;
        this.addressInvoice=addressInvoice;
    }

    public Address getAddressDelivery() {
        return addressDelivery;
    }

    public void setAddressDelivery(Address addressDelivery) {
        this.addressDelivery = addressDelivery;
    }

    public Address getAddressInvoice() {
        return addressInvoice;
    }

    public void setAddressInvoice(Address addressInvoice) {
        this.addressInvoice = addressInvoice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

	public Integer getIdAddressDelivery() {
		return idAddressDelivery;
	}

	public void setIdAddressDelivery(Integer idAddressDelivery) {
		this.idAddressDelivery = idAddressDelivery;
	}

	public Integer getIdAddressInvoice() {
		return idAddressInvoice;
	}

	public void setIdAddressInvoice(Integer idAddressInvoice) {
		this.idAddressInvoice = idAddressInvoice;
	}


}
