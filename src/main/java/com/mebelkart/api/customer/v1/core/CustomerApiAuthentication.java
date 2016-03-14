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
	CustomerAuthenticationDAO authenticationDAO;
	private String authenticatonKey;

	public CustomerApiAuthentication(CustomerAuthenticationDAO authenticationDAO, String authenticatonKey) {
		this.authenticationDAO = authenticationDAO;
		this.authenticatonKey = authenticatonKey;
	}

	/**
	 * @return
	 * It validates the apikey given by the user. 
	 */
	public boolean isAuthKeyValid() {
		List<CustomerAuthenticatonWrapper> customerAuthList = this.authenticationDAO.isCustomerValid(authenticatonKey.trim());
		if (customerAuthList.size() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return
	 * It checks whether the customer has permission to access the requests like GET,POST,PUT and DELETE.
	 * and it returns boolean result.
	 */
	public boolean isCustomerPermitted(int isCustomerHaveAccessToFunction){ //isCustomerHaveAccessToFunction
		List<CustomerAuthenticatonWrapper> customerAccessTokenlist = this.authenticationDAO.isCustomerValid(authenticatonKey.trim());
		if(customerAccessTokenlist.get(0).getIsHavingGetPermission() == isCustomerHaveAccessToFunction){
			return true;
		}
		return false;
		
	}
	
}
