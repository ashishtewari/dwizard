package com.mebelkart.api.util.cronTasks.classes;

public class OrderAffiliateInfo {
	private String orderId;
	private Integer affiliateId;
	private String affiliateOrderNumber;
	

	public OrderAffiliateInfo(String orderId, Integer affiliateId,String affiliateOrderNumber) {
		this.orderId = orderId;
		this.affiliateId = affiliateId;
		this.affiliateOrderNumber = affiliateOrderNumber;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getAffiliateId() {
		return affiliateId;
	}

	public void setAffiliateId(Integer affiliateId) {
		this.affiliateId = affiliateId;
	}

	public String getAffiliateOrderNumber() {
		return affiliateOrderNumber;
	}

	public void setAffiliateOrderNumber(String affiliateOrderNumber) {
		this.affiliateOrderNumber = affiliateOrderNumber;
	}

}
