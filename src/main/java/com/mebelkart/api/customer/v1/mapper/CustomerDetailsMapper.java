/**
 * 
 */
package com.mebelkart.api.customer.v1.mapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

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
public CustomerDetailsWrapper map(int index, ResultSet resultSet,
		StatementContext statement) throws SQLException {
	// TODO Auto-generated method stub
//	HashMap<String,String> mapDb = new HashMap<String,String>();
//	ResultSetMetaData resultMeta = resultSet.getMetaData();
//	int count=0;
//	while(resultSet.next()){
//	mapDb.put(resultMeta.getColumnName(count), resultSet.getString(resultMeta.getColumnName(count)));
//	count++;
//	}
	return new CustomerDetailsWrapper();
	}
}
