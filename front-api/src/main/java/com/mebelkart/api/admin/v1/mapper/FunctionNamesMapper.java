/**
 * 
 */
package com.mebelkart.api.admin.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.admin.v1.core.FunctionNames;

/**
 * @author Tinku
 *
 */
public class FunctionNamesMapper implements ResultSetMapper<FunctionNames> {

	public FunctionNames map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException{
		return new FunctionNames(resultSet.getInt("id"),resultSet.getString("a_function_name"));
	}

}
