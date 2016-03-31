package com.mebelkart.api.util.cronTasks.classes;

public class OrderMessageLang {

	private Integer orderMessageId;
	private Integer langId;
	private String name;
	private String message;
	

	public OrderMessageLang(Integer orderMessageId, Integer langId,String name, String message) {
		this.orderMessageId = orderMessageId;
		this.langId = langId;
		this.name = name;
		this.message = message;
	}

	public Integer getOrderMessageId() {
		return orderMessageId;
	}

	public void setOrderMessageId(Integer orderMessageId) {
		this.orderMessageId = orderMessageId;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
