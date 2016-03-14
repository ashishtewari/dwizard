/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerAuthenticatonWrapper {

	private int customerId,isHavingGetPermission;
	//private String firstname, lastname, email;

	public CustomerAuthenticatonWrapper(int customerId, int isHavingPermission) {
		// TODO Auto-generated constructor stub
		this.setCustomerId(customerId);
		this.setIsHavingGetPermission(isHavingPermission);
	}
	

//	public int getId() {
//		return customerId;
//	}
//
//	public void setId(int id) {
//		this.customerId = id;
//	}

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


	public int getCustomerId() {
		return customerId;
	}


	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

}
