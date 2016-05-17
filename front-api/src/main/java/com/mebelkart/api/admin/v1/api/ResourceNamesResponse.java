/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

import java.util.List;

import com.mebelkart.api.admin.v1.core.ResourceNames;

/**
 * @author Tinku
 *
 */
public class ResourceNamesResponse {
	
	private List<ResourceNames> resources;

	/**
	 * 
	 */
	public ResourceNamesResponse(List<ResourceNames> resources) {
		this.setResources(resources);
	}

	public List<ResourceNames> getResources() {
		return resources;
	}

	public void setResources(List<ResourceNames> resources) {
		this.resources = resources;
	}

}
