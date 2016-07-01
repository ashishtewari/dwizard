/**
 * 
 */
package com.mebelkart.api.product.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.product.v1.core.ProductReviewsWrapper;

/**
 * @author Tinku
 *
 */
public class ProductReviewsMapper implements ResultSetMapper<ProductReviewsWrapper>{

	public ProductReviewsWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException {
		return new ProductReviewsWrapper(resultSet.getInt("a_reviewid"), resultSet.getString("a_title"),
				resultSet.getString("a_content"), resultSet.getInt("a_rating"), resultSet.getString("a_permalink"),
				resultSet.getString("a_adddate"), resultSet.getInt("a_orderid"), resultSet.getString("a_customername"));
	}

}
