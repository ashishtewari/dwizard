package com.mebelkart.api.admin.v1.api;

import java.util.List;

import com.mebelkart.api.admin.v1.core.Privilages;

/**
 * @author Tinku
 *
 */
public class UserPrivilagesResponse {
	/**
	 * userName
	 */
	public String userName;
	/**
	 * privilages
	 */
	public List<Privilages> privileges;

	/**
	 * @param status
	 */
	public UserPrivilagesResponse(String userName, List<Privilages> privilages) {
		this.userName = userName;
		this.privileges = privilages;
	}
}
