/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

/**
 * @author Tinku
 *
 */
public class AdminPrivilagesResponse {
	
	private String[] addPrivilages;
	private String[] deletePrivilages;
	private String[] resourcePrivilages;
	/**
	 * Default Constructer
	 */
	public AdminPrivilagesResponse() {
		// TODO Auto-generated constructor stub
		this.setAddPrivilages(null);
		this.setDeletePrivilages(null);
		this.setResourcePrivilages(null);
	}
	public void setSuperAdminPrivilages(){
		this.setAddPrivilages(new String[] { "Admin", "Consumer" });
		this.setDeletePrivilages(new String[] { "Admin", "Consumer" });
		this.setResourcePrivilages(new String[] { "GET", "POST", "PUT", "DELETE" });
	}
	public void setAdminPrivilages(){
		this.setAddPrivilages(new String[] { "Consumer" });
		this.setDeletePrivilages(new String[] { "Consumer" });
		this.setResourcePrivilages(new String[] { "GET", "POST", "PUT" });
	}
	public String[] getAddPrivilages() {
		return addPrivilages;
	}
	public void setAddPrivilages(String[] addPrivilages) {
		this.addPrivilages = addPrivilages;
	}
	public String[] getDeletePrivilages() {
		return deletePrivilages;
	}
	public void setDeletePrivilages(String[] deletePrivilages) {
		this.deletePrivilages = deletePrivilages;
	}
	public String[] getResourcePrivilages() {
		return resourcePrivilages;
	}
	public void setResourcePrivilages(String[] resourcePrivilages) {
		this.resourcePrivilages = resourcePrivilages;
	}

}
