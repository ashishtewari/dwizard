package com.mebelkart.api.util.cronTasks.classes;

public class OrderStateLang {

	private Integer orderStateId;
	private Integer langId;
	private String name;
	private String template;
	
	public OrderStateLang(Integer orderStateId, Integer langId, String name,String template) {
		this.orderStateId = orderStateId;
		this.langId = langId;
		this.name = name;
		this.template = template;
	}
	
	public Integer getOrderStateId() {
		return orderStateId;
	}
	public void setOrderStateId(Integer orderStateId) {
		this.orderStateId = orderStateId;
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
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	
}
