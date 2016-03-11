/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import java.util.List;

import com.mebelkart.api.customer.v1.dao.CustomerDAO;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerApiAuthentication {

	/*
	 * authenticating the user
	 */
	CustomerDAO authentication;
	private String key;

	public CustomerApiAuthentication(CustomerDAO authentication, String key) {
		this.authentication = authentication;
		this.key = key;
	}

	public boolean isAuthKeyValid() {
		List<CustomerWrapper> list = this.authentication.isCustomerValid(key.trim());
		if (list.size() > 0 && key != "") {
			return true;
		}
		return false;
	}
	
	public boolean isCustomerPermitted(int authValue){
		List<CustomerWrapper> list = this.authentication.isCustomerValid(key.trim());
		if(list.get(0).getIsHavingGetPermission() == authValue){
			return true;
		}
		return false;
		
	}
	
//	public boolean isUserReachedAccessLimit(){
//		List<CustomerWrapper> list = this.authentication.getGreeting(key.trim());
//		if(list.get(0).a_getAccessCount() < list.get(0).getAccessCountLimit() && key != ""){
//			return true;
//		}
//		return false;
//		
//	}
}
