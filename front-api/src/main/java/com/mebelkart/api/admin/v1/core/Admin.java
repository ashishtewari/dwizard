package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class Admin {
	/**
     * a_user_name.
     */    
    private String a_user_name;
    
    /**
     * a_password.
     */    
    private String a_password;
    
    /**
     * a_admin_level.
     */
    private int a_admin_level;
    
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
    public Admin(String a_user_name, String a_password, int a_admin_level) {
    	this.setA_user_name(a_user_name);
    	this.setA_password(a_password);
    	this.setA_admin_level(a_admin_level);        
    }
    /**
	 * 
	 * @return
	 */
	public String getA_user_name() {
		return a_user_name;
	}
	/**
	 * 
	 * @param a_user_name
	 */
	public void setA_user_name(String a_user_name) {
		this.a_user_name = a_user_name;
	}
	/**
	 * 
	 * @return
	 */
	public String getA_password() {
		return a_password;
	}
	/**
	 * 
	 * @param a_password
	 */
	public void setA_password(String a_password) {
		this.a_password = a_password;
	}
	/**
	 * 
	 * @return
	 */
	public int getA_admin_level() {
		return a_admin_level;
	}
	/**
	 * 
	 * @param a_admin_level
	 */
	public void setA_admin_level(int a_admin_level) {
		this.a_admin_level = a_admin_level;
	}
	
}
