/**
 * 
 */
package com.mebelkart.api.admin.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.admin.v1.core.UserStatus;


/**
 * @author Tinku
 *
 */
public class UserStatusMapper implements ResultSetMapper<UserStatus>{

	public UserStatus map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new UserStatus(resultSet.getString("a_user_name"),
				resultSet.getLong("a_is_active"));
	}

}
