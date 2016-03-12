/**
 * 
 */
package com.mebelkart.api.customer.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;

/**
 * @author Nikky-Akky
 *
 */
public class CustomerDetailsMapper implements ResultSetMapper<CustomerDetailsWrapper>{

/* (non-Javadoc)
 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
 */
@Override
public CustomerDetailsWrapper map(int arg0, ResultSet resultSet,
		StatementContext arg2) throws SQLException {
	// TODO Auto-generated method stub
	return new CustomerDetailsWrapper(resultSet.getInt("id_customer"),resultSet.getString("firstname"),resultSet.getString("lastname"),resultSet.getString("email"),resultSet.getString("address1"),resultSet.getString("address2"),resultSet.getString("phone_mobile"),resultSet.getString("city"),resultSet.getString("postcode"),resultSet.getString("id_order"),resultSet.getString("total_paid"));
	}
}
