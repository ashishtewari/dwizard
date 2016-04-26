/**
 * 
 */
package com.mebelkart.api.manufacturer.v1.core;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerDetailsMapper implements ResultSetMapper<ManufacturerDetailsWrapper> {

	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	@Override
	public ManufacturerDetailsWrapper map(int arg0, ResultSet resultSet,
			StatementContext arg2) throws SQLException {
		// TODO Auto-generated method stub
		return new ManufacturerDetailsWrapper(resultSet.getInt("id_manufacturer"),resultSet.getInt("id_product"));
	}

}
