package com.mebelkart.api.product.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.product.v1.core.TopProductsWrapper;

public class TopProductsMapper implements ResultSetMapper<TopProductsWrapper>{

	public TopProductsWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new TopProductsWrapper(resultSet.getInt("product_id"), resultSet.getInt("product_quantity"));
	}

}
