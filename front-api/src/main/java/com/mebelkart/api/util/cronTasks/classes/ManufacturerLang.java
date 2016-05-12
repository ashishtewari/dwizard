/**
 * 
 */
package com.mebelkart.api.util.cronTasks.classes;

/**
 * @author Nikhil
 *
 */
public class ManufacturerLang {

	private Integer manufacturerId;
	private Integer langId;
	private String description;
	private String shortDescription;
	private String metaTitle;
	private String metaKeyWords;
	private String metaDescription;
	
	

	public ManufacturerLang(Integer manufacturerId, Integer langId,
			String description, String shortDescription, String metaTitle,
			String metaKeyWords, String metaDescription) {
		this.manufacturerId = manufacturerId;
		this.langId = langId;
		this.description = description;
		this.shortDescription = shortDescription;
		this.metaTitle = metaTitle;
		this.metaKeyWords = metaKeyWords;
		this.metaDescription = metaDescription;
	}

	public Integer getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(Integer manufacturerId) {
		this.manufacturerId = manufacturerId;
	}

	public Integer getLangId() {
		return langId;
	}

	public void setLangId(Integer langId) {
		this.langId = langId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		this.metaTitle = metaTitle;
	}

	public String getMetaKeyWords() {
		return metaKeyWords;
	}

	public void setMetaKeyWords(String metaKeyWords) {
		this.metaKeyWords = metaKeyWords;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public void setMetaDescription(String metaDescription) {
		this.metaDescription = metaDescription;
	}

}
