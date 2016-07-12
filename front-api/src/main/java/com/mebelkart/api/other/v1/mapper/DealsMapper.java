/**
 * 
 */
package com.mebelkart.api.other.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.other.v1.core.DealsWrapper;

/**
 * @author Tinku
 *
 */
public class DealsMapper implements ResultSetMapper<DealsWrapper>{
	
	public DealsWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException{
		return new DealsWrapper(resultSet.getInt("id_product"),resultSet.getString("flash_sale_date_end"),
				resultSet.getString("fs_availability"));
	}
}
