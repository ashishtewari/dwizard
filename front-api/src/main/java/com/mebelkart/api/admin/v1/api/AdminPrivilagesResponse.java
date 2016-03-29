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
	private String[] deletePrivileges;
	private String[] resourcePrivileges;
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
		return addPrivileges;
	}
	public void setAddPrivilages(String[] addPrivilages) {
		this.addPrivileges = addPrivilages;
	}
	public String[] getDeletePrivilages() {
		return deletePrivileges;
	}
	public void setDeletePrivilages(String[] deletePrivilages) {
		this.deletePrivileges = deletePrivilages;
	}
	public String[] getResourcePrivilages() {
		return resourcePrivileges;
	}
	public void setResourcePrivilages(String[] resourcePrivilages) {
		this.resourcePrivileges = resourcePrivilages;
	}

}
