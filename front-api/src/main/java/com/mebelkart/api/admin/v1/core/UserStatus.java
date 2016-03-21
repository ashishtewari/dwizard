/**
 * 
 */
package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class UserStatus {
	
	/**
	 * userName
	 */
	private String userName;
	/**
	 * activeStatus
	 */
	private long activeStatus;
	/**
	 * Default Constructer
	 */
	public UserStatus() {
		// TODO Auto-generated constructor stub
		this.activeStatus = 0;
		this.userName = null;
	}
	
	public UserStatus(String userName,long activeStatus) {
		// TODO Auto-generated constructor stub
		this.setUserName(userName);
		this.setActiveStatus(activeStatus);
	}
	/**
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return
	 */
	public long getActiveStatus() {
		return activeStatus;
	}
	/**
	 * @param activeStatus
	 */
	public void setActiveStatus(long activeStatus) {
		this.activeStatus = activeStatus;
	}

}
