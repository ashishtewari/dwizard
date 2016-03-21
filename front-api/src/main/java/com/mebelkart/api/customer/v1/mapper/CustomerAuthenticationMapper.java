/**
 * 
 */
package com.mebelkart.api.customer.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.customer.v1.core.CustomerAuthenticatonWrapper;


/**
 * @author Nikky-Akky
 *
 */
public class CustomerAuthenticationMapper implements ResultSetMapper<CustomerAuthenticatonWrapper>{

	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	/* 
	 * Mapping the resultset to the object and returning it.
	 */
	@Override
	public CustomerAuthenticatonWrapper map(int index, ResultSet resultSet, StatementContext statement)
			throws SQLException {
		return new CustomerAuthenticatonWrapper(resultSet.getInt("id"),resultSet.getInt("a_is_active"),resultSet.getInt("a_have_get_permission"));
		
	}
	
}
