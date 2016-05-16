/**
 * 
 */
package com.mebelkart.api.admin.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.admin.v1.core.Privilages;


/**
 * @author Tinku
 *
 */
public class PrivilagesMapper implements ResultSetMapper<Privilages>{

	public Privilages map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new Privilages(resultSet.getLong("id"),resultSet.getString("a_resource_name"),
				resultSet.getLong("a_have_get_permission"),
				resultSet.getLong("a_have_post_permission"),
				resultSet.getLong("a_have_put_permission"),
				resultSet.getLong("a_have_delete_permission"));
	}
}
