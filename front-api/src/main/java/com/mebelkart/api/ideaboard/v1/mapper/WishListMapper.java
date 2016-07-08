/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.ideaboard.v1.core.WishListWrapper;

/**
 * @author Tinku
 *
 */
public class WishListMapper implements ResultSetMapper<WishListWrapper> {

	public WishListWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext ) throws SQLException {
		return new WishListWrapper(resultSet.getInt("id_wishlist"),resultSet.getString("name"),
				resultSet.getString("token"), resultSet.getString("date_add"), resultSet.getString("date_upd"),
				resultSet.getInt("counter"));
	}

}
