/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.mebelkart.api.customer.v1.core.CustomerWrapper;
import com.mebelkart.api.customer.v1.mapper.CustomerMapper;

/**
 * @author Nikky-Akky
 *
 */
@RegisterMapper(CustomerMapper.class)
public interface CustomerDAO {


	@SqlQuery("SELECT mk_api_consumer.id,mk_api_resources_consumer_permission.a_have_get_permission FROM mk_api_consumer JOIN mk_api_resources_consumer_permission ON mk_api_consumer.id = mk_api_resources_consumer_permission.a_consumer_id WHERE mk_api_consumer.a_access_token = :key")
	List<CustomerWrapper> isCustomerValid(@BindBean("")String key);



}
