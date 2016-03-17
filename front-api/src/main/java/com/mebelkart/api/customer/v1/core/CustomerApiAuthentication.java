/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import java.util.List;

import com.mebelkart.api.customer.v1.dao.CustomerAuthenticationDAO;
import com.mebelkart.api.customer.v1.dao.CustomerDetailsDAO;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerApiAuthentication {

	/*
	 * authenticating the user
	 */
	CustomerAuthenticationDAO authenticationDao;
	CustomerDetailsDAO customerDetailsDao;
	private String authenticatonKey;

	public CustomerApiAuthentication(CustomerAuthenticationDAO authenticationDao, CustomerDetailsDAO customerDetailsDao, String authenticatonKey) {
		this.authenticationDao = authenticationDao;
		this.customerDetailsDao = customerDetailsDao;
		this.authenticatonKey = authenticatonKey;
	}

	/**
	 * @return true if the apikey given by the user and also key is active.
	 *   
	 */
	public boolean isAuthKeyValid() {
		List<CustomerAuthenticatonWrapper> customerAuthList = this.authenticationDao.isCustomerValid(authenticatonKey.trim());
		List<CustomerAuthenticatonWrapper> adminAuthList = this.authenticationDao.isAdminValid(authenticatonKey.trim());
		System.out.println("size in authkeyvalid = " + adminAuthList.size());
		if ((customerAuthList.size() > 0 && customerAuthList.get(0).getIsActive()==1) || (adminAuthList.size() > 0 && adminAuthList.get(0).getIsActive()==1)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if the customer has permission to access the requests like GET,POST,PUT and DELETE.
	 */
	public boolean isCustomerPermitted(){ 
		List<CustomerAuthenticatonWrapper> customerAccessTokenlist = this.authenticationDao.isCustomerValid(authenticatonKey.trim());
		List<CustomerAuthenticatonWrapper> adminAccessTokenList = this.authenticationDao.isAdminValid(authenticatonKey.trim());
		if((customerAccessTokenlist.size() > 0 && customerAccessTokenlist.get(0).getIsHavingGetPermission() == 1) || (adminAccessTokenList.size() > 0 &&  adminAccessTokenList.get(0).getIsHavingGetPermission() == 1)){
			return true;
		}
		return false;
		
	}

	/**
	 * @param customerId
	 * @return true if customerId is valid else false.
	 */
	public boolean isCustomerIdValid(long customerId) {
		// TODO Auto-generated method stub
		List<CustomerDetailsWrapper> customerIdList = this.customerDetailsDao.getCustomerId(customerId);
		if(customerIdList.size()>0){
			return true;
		}
		return false;
	}
	
}
