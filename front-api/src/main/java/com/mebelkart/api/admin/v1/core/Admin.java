package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class Admin {
	/**
     * a_user_name.
     */    
    private String userName;
    
    /**
     * a_password.
     */    
    private String password;
    
    /**
     * a_admin_level.
     */
    private int adminLevel;
    
    /**
     *   A default constructor to create Admin.
     */
    public Admin() {
    }
    /**
     * A constructor to create Admin.
     * @param id
     * @param a_user_name
     * @param a_password
     * @param a_admin_level
     */
    public Admin(String userName, String password, int adminLevel) {
    	this.setUserName(userName);
    	this.setPassword(password);
    	this.setAdminLevel(adminLevel);     
    }
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getAdminLevel() {
		return adminLevel;
	}
	public void setAdminLevel(int adminLevel) {
		this.adminLevel = adminLevel;
	}
}
