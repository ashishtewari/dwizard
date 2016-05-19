package com.mebelkart.api.admin.v1.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.mebelkart.api.admin.v1.core.Privilages;

/**
 * @author Tinku
 *
 */
@JsonInclude(Include.NON_NULL)
public class UserPrivilagesResponse {
	/**
	 * userName
	 */
	public String userName;
	public Integer rateLimit;
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
	
	public UserPrivilagesResponse(String userName, Integer rateLimit, List<Privilages> privilages) {
		this.userName = userName;
		this.rateLimit = rateLimit;
		this.privileges = privilages;
	}
}
