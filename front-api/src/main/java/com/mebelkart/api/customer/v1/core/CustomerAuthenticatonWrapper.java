/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerAuthenticatonWrapper {

	private int id,isHavingGetPermission;
	//private String firstname, lastname, email;

	public CustomerAuthenticatonWrapper(int id, int isHavingPermission) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.isHavingGetPermission = isHavingPermission;
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

//	public String getFirstname() {
//		return firstname;
//	}
//
//	public void setFirstname(String firstname) {
//		this.firstname = firstname;
//	}
//
//	public String getLastname() {
//		return lastname;
//	}
//
//	public void setLastname(String lastname) {
//		this.lastname = lastname;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}

	public int getIsHavingGetPermission() {
		return isHavingGetPermission;
	}

	public void setIsHavingGetPermission(int isHavingGetPermission) {
		this.isHavingGetPermission = isHavingGetPermission;
	}

}
