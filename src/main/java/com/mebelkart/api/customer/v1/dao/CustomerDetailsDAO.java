/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;


import com.github.rkmk.mapper.CustomMapperFactory;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;

/**
 * @author Nikky-Akky
 * return all details of a particular customer.
 *
 */

public interface CustomerDetailsDAO {
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT ps_customer.id_customer,ps_customer.firstname,ps_customer.lastname,ps_customer.email,ps_address.address1,ps_address.address2,ps_address.phone_mobile,ps_address.city,ps_address.postcode,ps_orders.id_order AS orders$id_order,ps_orders.total_paid AS orders$total_paid FROM ps_customer JOIN ps_address ON ps_customer.id_customer=ps_address.id_customer JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer WHERE ps_customer.id_customer=:customerId")
	FoldingList<CustomerDetailsWrapper>getCustomerDetails(@Bind("customerId")long customerId);
	
	@SqlQuery("SELECT ps_customer.firstname FROM ps_customer WHERE ps_customer.id_customer=:customerId")
	FoldingList<CustomerDetailsWrapper>getCustomerPersonalDetails(@Bind("customerId")long customerID);
	
	@SqlQuery("SELECT ps_customer.lastname FROM ps_customer WHERE ps_customer.id_customer=:customerId")
	FoldingList<CustomerDetailsWrapper>getCustomerPersonalDetail(@Bind("customerId")long customerID);
}

