/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import com.mebelkart.api.customer.v1.core.CustomerAuthenticatonWrapper;
import com.mebelkart.api.customer.v1.mapper.CustomerAuthenticationMapper;

/**
 * @author Nikky-Akky
 *
 */
@RegisterMapper(CustomerAuthenticationMapper.class)
public interface CustomerAuthenticationDAO {


	/**
	 * Query for checking if the user is customer and authenticating the user and checking the authorization, like whether he has access to particular
	 * method or not.
	 */
	@SqlQuery("SELECT mk_api_consumer.id,mk_api_consumer.a_is_active,mk_api_resources_consumer_permission.a_have_get_permission FROM mk_api_consumer JOIN mk_api_resources_consumer_permission ON mk_api_consumer.id = mk_api_resources_consumer_permission.a_consumer_id WHERE mk_api_consumer.a_access_token = :authenticatonKey")
	List<CustomerAuthenticatonWrapper> isCustomerValid(@Bind("authenticatonKey")String authenticatonKey);
	
	/**
	 * Query for checking if the user is admin and authenticating the admin and checking the authorization, like whether he has access to particular
	 * method or not.
	 */
	@SqlQuery("SELECT mk_api_user_admin.id,mk_api_user_admin.a_is_active,mk_api_resources_admin_permission.a_have_get_permission FROM mk_api_user_admin JOIN mk_api_resources_admin_permission ON mk_api_user_admin.id = mk_api_resources_admin_permission.a_admin_id WHERE mk_api_user_admin.a_access_token = :authenticatonKey")
	List<CustomerAuthenticatonWrapper> isAdminValid(@Bind("authenticatonKey")String authenticatonKey);



}
