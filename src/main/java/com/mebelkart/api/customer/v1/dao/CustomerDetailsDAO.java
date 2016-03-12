/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;
import com.mebelkart.api.customer.v1.mapper.CustomerDetailsMapper;

/**
 * @author Nikky-Akky
 * return all details of a particular customer.
 *
 */
@RegisterMapper(CustomerDetailsMapper.class)
public interface CustomerDetailsDAO {
	
	@SqlQuery("SELECT ps_customer.id_customer,ps_customer.firstname,ps_customer.lastname,ps_customer.email,ps_address.address1,ps_address.address2,ps_address.phone_mobile,ps_address.city,ps_address.postcode,ps_orders.id_order,ps_orders.total_paid FROM ps_customer JOIN ps_address ON ps_customer.id_customer=ps_address.id_customer JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer WHERE ps_customer.id_customer=:id")
	List<CustomerDetailsWrapper>getCustomerDetails(@Bind("id")int id);
	
}
