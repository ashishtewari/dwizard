/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

//import java.math.BigDecimal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.OneToMany;

/**
 * @author Nikky-Akky
 *
 */
@JsonInclude(Include.NON_NULL)
public class CustomerDetailsWrapper {
	//private int customerId;
	private String firstName;
	private String lastName;
	private String email;
	private String address1;
	private String address2;
	private String mobile;
	private String city;
	@ColumnName("name")
	private String state;
	private String postCode;
	 @OneToMany("orders")
	private List<CustomerOrdersWrapper> orders;

//	public CustomerDetailsWrapper(int customerId, String firstname, String lastname,
//			String email, String address1, String address2, String mobile,
//			String city, String postcode, String orderId, String totalpaid) {
//		// TODO Auto-generated constructor stub
//		this.setCustomerId(customerId);
//		this.setFirstName(firstname);
//		this.setLastName(lastname);
//		this.setEmail(email);
//		this.setAddress1(address1);
//		this.setAddress2(address2);
//		this.setMobile(mobile);
//		this.setCity(city);
//		this.setPostCode(postcode);
//		this.setOrderId(orderId);
//		this.setTotalPaid(totalpaid);
//	}

	public List<CustomerOrdersWrapper> getOrders() {
		return orders;
	}

	public void setOrders(List<CustomerOrdersWrapper> orders) {
		this.orders = orders;
	}

//	public CustomerDetailsWrapper(BigDecimal bigDecimal) {
//		// TODO Auto-generated constructor stub
//		
//	}


//	public int getCustomerId() {
//		return customerId;
//	}
//
//	public void setCustomerId(int customerId) {
//		this.customerId = customerId;
//	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
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

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
