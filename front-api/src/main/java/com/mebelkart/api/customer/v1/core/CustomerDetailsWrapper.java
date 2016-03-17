/**
 * 
 */
package com.mebelkart.api.customer.v1.core;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.OneToMany;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikky-Akky
 *
 */
@JsonInclude(Include.NON_NULL)
public class CustomerDetailsWrapper {
	
	@PrimaryKey()
	@ColumnName("id_customer")
	private int customerId;
	private String firstName;
	private String lastName;
	private String email;
	@OneToMany("address")
	private List<CustomerAddressWrapper> address;
	@OneToMany("wishlist")
	private List<CustomerWishListWrapper> wishList;
	@OneToMany("orders")
	private List<CustomerOrdersWrapper> orders;

	public List<CustomerAddressWrapper> getAddress() {
		return address;
	}

	public void setAddress(List<CustomerAddressWrapper> address) {
		this.address = address;
	}

	public List<CustomerWishListWrapper> getWishList() {
		return wishList;
	}

	public void setWishList(List<CustomerWishListWrapper> wishList) {
		this.wishList = wishList;
	}

	// public CustomerDetailsWrapper(int customerId, String firstname, String
	// lastname,
	// String email, String address1, String address2, String mobile,
	// String city, String postcode, String orderId, String totalpaid) {
	// // TODO Auto-generated constructor stub
	// this.setCustomerId(customerId);
	// this.setFirstName(firstname);
	// this.setLastName(lastname);
	// this.setEmail(email);
	// this.setAddress1(address1);
	// this.setAddress2(address2);
	// this.setMobile(mobile);
	// this.setCity(city);
	// this.setPostCode(postcode);
	// this.setOrderId(orderId);
	// this.setTotalPaid(totalpaid);
	// }

	public List<CustomerOrdersWrapper> getOrders() {
		return orders;
	}

	public void setOrders(List<CustomerOrdersWrapper> orders) {
		this.orders = orders;
	}

	// public CustomerDetailsWrapper(BigDecimal bigDecimal) {
	// // TODO Auto-generated constructor stub
	//
	// }

	 public int getCustomerId() {
	 return customerId;
	 }
	
	 public void setCustomerId(int customerId) {
	 this.customerId = customerId;
	 }

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

}
