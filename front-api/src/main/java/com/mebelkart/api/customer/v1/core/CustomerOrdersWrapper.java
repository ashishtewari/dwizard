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
public class CustomerOrdersWrapper {
	@PrimaryKey()
	@ColumnName("id_order")
	private int orderId;
	@ColumnName("total_paid")
	private String totalPaid;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(String totalPaid) {
		this.totalPaid = totalPaid;
	}

}
