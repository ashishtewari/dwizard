/**
 * 
 */
package com.mebelkart.api.order.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.order.v1.core.OrderDetailSellerStatuses;

/**
 * @author Tinku
 *
 */
public class OrderDetailSellerStatusesMapper implements ResultSetMapper<OrderDetailSellerStatuses> {

	public OrderDetailSellerStatuses map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new OrderDetailSellerStatuses(resultSet.getString("status_name"), resultSet.getInt("active"), resultSet.getInt("display_to_seller"));
	}

}
