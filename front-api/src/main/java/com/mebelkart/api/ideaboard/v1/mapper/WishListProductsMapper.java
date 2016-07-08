/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.mebelkart.api.ideaboard.v1.core.WishListProductsWrapper;

/**
 * @author Tinku
 *
 */
public class WishListProductsMapper implements ResultSetMapper<WishListProductsWrapper>{

	public WishListProductsWrapper map(int i, ResultSet resultSet,
			StatementContext statementContext) throws SQLException{
		return new WishListProductsWrapper(resultSet.getInt("id_product"), resultSet.getInt("quantity"),
				resultSet.getInt("product_quantity"), resultSet.getString("name"), resultSet.getInt("id_product_attribute"),
				resultSet.getInt("priority"), resultSet.getString("link_rewrite"), resultSet.getString("category_rewrite"));
	}

}
