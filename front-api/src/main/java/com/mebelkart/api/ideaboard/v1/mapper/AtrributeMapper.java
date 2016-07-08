/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.ideaboard.v1.core.AttributeWrapper;

/**
 * @author Tinku
 *
 */
public class AtrributeMapper implements ResultSetMapper<AttributeWrapper>{	
	public AttributeWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new AttributeWrapper(resultSet.getString("attribute_name"), resultSet.getInt("attribute_quantity"));
	}
}
