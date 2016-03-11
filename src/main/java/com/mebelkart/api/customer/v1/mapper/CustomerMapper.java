/**
 * 
 */
package com.mebelkart.api.customer.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.customer.v1.core.CustomerWrapper;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerMapper implements ResultSetMapper<CustomerWrapper>{

	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
	public CustomerWrapper map(int arg0, ResultSet resultSet, StatementContext arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		return new CustomerWrapper(resultSet.getInt("id"),resultSet.getInt("a_have_get_permission"));
		
	}
	
}
