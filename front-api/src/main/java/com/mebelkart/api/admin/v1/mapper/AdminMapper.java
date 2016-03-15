package com.mebelkart.api.admin.v1.mapper;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.admin.v1.core.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Tinku
 *
 */
public class AdminMapper implements ResultSetMapper<Admin> {
	/* (non-Javadoc)
	 * @see org.skife.jdbi.v2.tweak.ResultSetMapper#map(int, java.sql.ResultSet, org.skife.jdbi.v2.StatementContext)
	 */
	public Admin map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new Admin(resultSet.getInt("id"),
				resultSet.getString("a_user_name"),
				resultSet.getString("a_password"),
				resultSet.getInt("a_admin_level"));
	}
}
