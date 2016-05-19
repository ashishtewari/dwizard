/**
 * 
 */
package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class ResourceNames {

	private String resourceName;
	private Integer resourceId;

	/**
	 * Default Constructer
	 */
	public ResourceNames() {
	}
	
	public ResourceNames(Integer resourceId,String resourceName) {
		this.setResourceName(resourceName);
		this.setResourceId(resourceId);
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Integer getResourceId() {
		return resourceId;
	}

	public void setResourceId(Integer resourceId) {
		this.resourceId = resourceId;
	}

}
