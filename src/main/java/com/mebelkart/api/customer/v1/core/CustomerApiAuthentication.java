/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import java.util.List;

import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerApiAuthentication {

	/*
	 * authenticating the user
	 */
	CustomerAuthenticationDAO authentication;
	private String key;

	public CustomerApiAuthentication(CustomerAuthenticationDAO authentication, String key) {
		this.authentication = authentication;
		this.key = key;
	}

	/**
	 * @return
	 * It validates the apikey given by the user. 
	 */
	public boolean isAuthKeyValid() {
		System.out.println("entered in auth key valid");
		List<CustomerAuthenticatonWrapper> list = this.authentication.isCustomerValid(key.trim());
		System.out.println("key = " + key);
		System.out.println("size = "+list.size());
		if (list.size() > 0 && key != "") {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 * It checks whether the customer has permission to access the requests like GET,POST,PUT and DELETE.
	 * and it returns boolean result.
	 */
	public boolean isCustomerPermitted(int authValue){
		List<CustomerAuthenticatonWrapper> list = this.authentication.isCustomerValid(key.trim());
		System.out.println("auth value = " + list.get(0).getIsHavingGetPermission());
		if(list.get(0).getIsHavingGetPermission() == authValue){
			return true;
		}
		return false;
		
	}
	
}
