/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikky-Akky
 *
 */
@JsonInclude(Include.NON_NULL)
public class CustomerAddressWrapper {
	@PrimaryKey()
	@ColumnName("id_address")
	private int addressId;
	@ColumnName("address1")
	private String address1;
	@ColumnName("address2")
	private String address2;
	@ColumnName("phone_mobile")
	private String mobile;
	@ColumnName("city")
	private String city;
	@ColumnName("name")
	private String state;
	@ColumnName("postcode")
	private String postCode;

	public int getAddressId() {
		return addressId;
	}

	public void setAddressId(int addressId) {
		this.addressId = addressId;
	}

	public String getAddress1() {
		if (!(address1 == null)) {
			address1 = address1.replaceAll("\\r\\n", "");
		}
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		if (!(address2 == null)) {
			address2 = address2.replaceAll("\\r\\n", "");
		}
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

}
