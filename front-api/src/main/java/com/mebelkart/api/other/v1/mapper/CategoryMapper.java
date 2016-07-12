/**
 * 
 */
package com.mebelkart.api.other.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.other.v1.core.CategoryWrapper;

/**
 * @author Tinku
 *
 */
public class CategoryMapper implements ResultSetMapper<CategoryWrapper>{
	
	public CategoryWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new CategoryWrapper(resultSet.getInt("id_category"),resultSet.getString("name"));
	}

}
