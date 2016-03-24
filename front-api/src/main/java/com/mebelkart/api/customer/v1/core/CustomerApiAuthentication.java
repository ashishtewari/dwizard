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

	public CustomerApiAuthentication(CustomerAuthenticationDAO authenticationDao, CustomerDetailsDAO customerDetailsDao) {
		this.authenticationDao = authenticationDao;
		this.customerDetailsDao = customerDetailsDao;
	}

	/**
	 * @return true if the apikey given by the user and also key is active.
	 *   
	 */
	public boolean isAuthKeyValid(String authenticationKey,String typeOfPermission) {
		List<CustomerAuthenticatonWrapper> customerAuthList = this.authenticationDao.isCustomerValid(authenticationKey.trim());
		List<CustomerAuthenticatonWrapper> adminAuthList = this.authenticationDao.isAdminValid(authenticationKey.trim());
		if ((customerAuthList.size() > 0 && customerAuthList.get(0).getIsActive()==1) || (adminAuthList.size() > 0 && adminAuthList.get(0).getIsActive()==1)) {
			return true;
		}
		return false;
	}
	
	/**
	 * @return true if the customer has permission to access the requests like GET,POST,PUT and DELETE.
	 */
	public boolean isCustomerPermitted(String authenticationKey,String typeOfPermission){ 
		List<CustomerAuthenticatonWrapper> customerAccessTokenlist = this.authenticationDao.isCustomerValid(authenticationKey.trim());
		List<CustomerAuthenticatonWrapper> adminAccessTokenList = this.authenticationDao.isAdminValid(authenticationKey.trim());
		if(typeOfPermission.equals("get")){
			if((customerAccessTokenlist.size() > 0 && customerAccessTokenlist.get(0).getIsHavingGetPermission() == 1) || (adminAccessTokenList.size() > 0 &&  adminAccessTokenList.get(0).getIsHavingGetPermission() == 1)){
				return true;
			}
		}else if(typeOfPermission.equals("post")){
			if((customerAccessTokenlist.size() > 0 && customerAccessTokenlist.get(0).getIsHavingPostPermission() == 1) || (adminAccessTokenList.size() > 0 &&  adminAccessTokenList.get(0).getIsHavingPostPermission() == 1)){
				return true;
			}
		}else if(typeOfPermission.equals("put")){
			if((customerAccessTokenlist.size() > 0 && customerAccessTokenlist.get(0).getIsHavingPutPermission() == 1) || (adminAccessTokenList.size() > 0 &&  adminAccessTokenList.get(0).getIsHavingPutPermission() == 1)){
				return true;
			}
		}else if(typeOfPermission.equals("delete")){
			if((customerAccessTokenlist.size() > 0 && customerAccessTokenlist.get(0).getIsHavingDeletePermission() == 1) || (adminAccessTokenList.size() > 0 &&  adminAccessTokenList.get(0).getIsHavingDeletePermission() == 1)){
				return true;
			}
		}
		return false;
	}

	/**
	 * @param customerId
	 * @return true if customerId is valid else false.
	 */
	public boolean isCustomerIdValid(long customerId) {
		List<CustomerDetailsWrapper> customerIdList = this.customerDetailsDao.getCustomerId(customerId);
		if(customerIdList.size()>0){
			return true;
		}
		return false;
	}
	
}
