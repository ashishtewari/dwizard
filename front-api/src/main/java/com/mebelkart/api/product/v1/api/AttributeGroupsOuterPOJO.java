/**
 * 
 */
package com.mebelkart.api.product.v1.api;

/**
 * @author Tinku
 *
 */
public class AttributeGroupsOuterPOJO {
	
	private Object groups;
	private Object attributeMappings;

	/**
	 * Parameterized Constructer
	 */
	public AttributeGroupsOuterPOJO(Object groups,Object attributeMappings) {
		this.setGroups(groups);
		this.setAttributeMappings(attributeMappings);
	}

	public Object getGroups() {
		return groups;
	}

	public void setGroups(Object groups) {
		this.groups = groups;
	}

	public Object getAttributeMappings() {
		return attributeMappings;
	}

	public void setAttributeMappings(Object attributeMappings) {
		this.attributeMappings = attributeMappings;
	}

}
