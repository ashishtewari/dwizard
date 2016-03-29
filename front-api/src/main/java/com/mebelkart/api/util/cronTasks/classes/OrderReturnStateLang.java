package com.mebelkart.api.util.cronTasks.classes;

public class OrderReturnStateLang {

	private Integer orderReturnStateId;
	private Integer langId;
	private String name;

	public OrderReturnStateLang(Integer orderReturnStateId, Integer langId,String name) {
		this.orderReturnStateId = orderReturnStateId;
		this.langId = langId;
		this.name = name;
	}

	public Integer getOrderReturnStateId() {
		return orderReturnStateId;
	}

	public void setOrderReturnStateId(Integer orderReturnStateId) {
		this.orderReturnStateId = orderReturnStateId;
	}

	public Integer getLangId() {
		return langId;
	}

	public void setLangId(Integer langId) {
		this.langId = langId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
