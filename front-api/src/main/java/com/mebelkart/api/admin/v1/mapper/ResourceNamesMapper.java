/**
 * 
 */
package com.mebelkart.api.admin.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.admin.v1.core.ResourceNames;

/**
 * @author Tinku
 *
 */
public class ResourceNamesMapper implements ResultSetMapper<ResourceNames>{

	public ResourceNames map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException{
		return new ResourceNames(resultSet.getInt("id"),resultSet.getString("a_resource_name"));
	}

}
