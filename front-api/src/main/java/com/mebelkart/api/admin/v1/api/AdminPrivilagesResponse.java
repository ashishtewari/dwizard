/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Tinku
 *
 */
@JsonInclude(Include.NON_NULL)
public class AdminPrivilagesResponse {
	
	private String sessionId; 
//	private String[] addPrivileges;
//	private String[] resourcePrivileges;
//	private String[] methodPrivilages;
	private String userLevel;
	/**
	 * Default Constructer
	 */
	public AdminPrivilagesResponse() {
//		this.setAddPrivilages(null);
//		this.setResourcePrivilages(null);
//		this.setMethodPrivilages(null);
		this.setSessionId(null);
		this.setUserLevel(null);
	}
//	public void setSuperAdminPrivilages(String[] resourcePrivilages){
//		this.setAddPrivilages(new String[] { "Admin", "Consumer" });
//		this.setResourcePrivilages(resourcePrivilages);
//		this.setMethodPrivilages(new String[] { "GET", "POST", "PUT" });
//	}
//	public void setAdminPrivilages(String[] resourcePrivilages){
//		this.setAddPrivilages(new String[] { "Consumer" });
//		this.setResourcePrivilages(resourcePrivilages);
//		this.setMethodPrivilages(new String[] { "GET" });
//	}
//	public String[] getAddPrivilages() {
//		return addPrivileges;
//	}
//	public void setAddPrivilages(String[] addPrivilages) {
//		this.addPrivileges = addPrivilages;
//	}
//	public String[] getResourcePrivilages() {
//		return resourcePrivileges;
//	}
//	public void setResourcePrivilages(String[] resourcePrivilages) {
//		this.resourcePrivileges = resourcePrivilages;
//	}
//	public String[] getMethodPrivilages() {
//		return methodPrivilages;
//	}
//	public void setMethodPrivilages(String[] methodPrivilages) {
//		this.methodPrivilages = methodPrivilages;
//	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}
	public String getUserLevel(){
		return userLevel;
	}
}
