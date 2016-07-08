/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.ideaboard.v1.core.NBProductsWrapper;

/**
 * @author Tinku
 *
 */
public class NBProductsMapper implements ResultSetMapper<NBProductsWrapper> {
	
	public NBProductsWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new NBProductsWrapper(resultSet.getInt("nbProducts"),resultSet.getInt("id_wishlist"));
	}
	
}
