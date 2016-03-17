/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import java.util.List;

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
	@SqlQuery("SELECT ps_customer.firstname,ps_customer.lastname,ps_customer.email,ps_address.address1,ps_address.address2,ps_address.phone_mobile,ps_address.city,ps_state.name,ps_address.postcode,ps_orders.id_order AS orders$id_order,ps_orders.total_paid AS orders$total_paid FROM ps_customer JOIN ps_address ON ps_customer.id_customer=ps_address.id_customer JOIN ps_state ON ps_address.id_state=ps_state.id_state LEFT JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer WHERE ps_customer.id_customer= :customerId")
	FoldingList<CustomerDetailsWrapper>getCustomerDetails(@Bind("customerId")long customerId);

	/**
	 * @param customerId
	 * @return
	 */
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT ps_customer.email from ps_customer where ps_customer.id_customer = :customerId")
	List<CustomerDetailsWrapper> getCustomerId(@Bind("customerId")long customerId);
}

