package com.mebelkart.api.util.cronTasks.classes;

import java.util.List;

/**
 * Created by vinitpayal on 23/03/16.
 */
public class Order {
    private Integer orderId;
    private Integer customerId;
    private Integer cartId;
    private Integer currencyId;
    private String paymentMode;
    private String module;
    private List<OrderDetail> orderDetails;
    private List<OrderDetailLogisticService> logisticDetails;

    public List<OrderDetailLogisticService> getLogisticDetails() {
        return logisticDetails;
    }

    public void setLogisticDetails(List<OrderDetailLogisticService> logisticDetails) {
        this.logisticDetails = logisticDetails;
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
}
