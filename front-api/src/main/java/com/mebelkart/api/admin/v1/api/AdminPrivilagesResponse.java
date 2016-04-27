/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class AdminPrivilagesResponse {
	
	private String[] addPrivileges;
	private String[] resourcePrivileges;
	private String[] methodPrivilages;
	/**
	 * Default Constructer
	 */
	public AdminPrivilagesResponse() {
		// TODO Auto-generated constructor stub
		this.setAddPrivilages(null);
		this.setResourcePrivilages(null);
		this.setMethodPrivilages(null);
	}
	public void setSuperAdminPrivilages(String[] resourcePrivilages){
		this.setAddPrivilages(new String[] { "Admin", "Consumer" });
		this.setResourcePrivilages(resourcePrivilages);
		this.setMethodPrivilages(new String[] { "GET", "POST", "PUT" });
	}
	public void setAdminPrivilages(String[] resourcePrivilages){
		this.setAddPrivilages(new String[] { "Consumer" });
		this.setResourcePrivilages(resourcePrivilages);
		this.setMethodPrivilages(new String[] { "GET" });
	}
	public String[] getAddPrivilages() {
		return addPrivileges;
	}
	public void setAddPrivilages(String[] addPrivilages) {
		this.addPrivileges = addPrivilages;
	}
	public String[] getResourcePrivilages() {
		return resourcePrivileges;
	}
	public void setResourcePrivilages(String[] resourcePrivilages) {
		this.resourcePrivileges = resourcePrivilages;
	}
	public String[] getMethodPrivilages() {
		return methodPrivilages;
	}
	public void setMethodPrivilages(String[] methodPrivilages) {
		this.methodPrivilages = methodPrivilages;
	}

}
