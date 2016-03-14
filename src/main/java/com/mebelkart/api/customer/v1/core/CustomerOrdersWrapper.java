/**
 * 
 */
package com.mebelkart.api.customer.v1.core;

import com.github.rkmk.annotations.ColumnName;
import com.github.rkmk.annotations.PrimaryKey;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerOrdersWrapper {
	@PrimaryKey()
	@ColumnName("id_order")
	private int orderId;
	private int totalPaid;

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getTotalPaid() {
		return totalPaid;
	}

	public void setTotalPaid(int totalPaid) {
		this.totalPaid = totalPaid;
	}
}
