/**
 * 
 */
package com.mebelkart.api.product.v1.api;

import java.util.Map;

/**
 * @author Tinku
 *
 */
public class AttributeGroupsInnerPOJO {
	
	private String name;
	private String colorGroup;
	private int defaultAttribute;
	private Map<String,Map<String,Object>> attributes;

	/**
	 * Parameterised Constructer
	 */
	public AttributeGroupsInnerPOJO(String name, String colorGroup, int defaultAttribute, Map<String,Map<String,Object>> attributes) {
		this.setName(name);
		this.setColorGroup(colorGroup);
		this.setDefaultAttribute(defaultAttribute);
		this.setAttributes(attributes);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColorGroup() {
		return colorGroup;
	}

	public void setColorGroup(String colorGroup) {
		this.colorGroup = colorGroup;
	}

	public int getDefaultAttribute() {
		return defaultAttribute;
	}

	public void setDefaultAttribute(int defaultAttribute) {
		this.defaultAttribute = defaultAttribute;
	}

	public Map<String,Map<String,Object>> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String,Map<String,Object>> attributes) {
		this.attributes = attributes;
	}

}
