/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.ideaboard.v1.core.WishListWrapper;
import com.mebelkart.api.ideaboard.v1.core.NBProductsWrapper;
import com.mebelkart.api.ideaboard.v1.mapper.WishListMapper;
import com.mebelkart.api.ideaboard.v1.mapper.NBProductsMapper;
import com.mebelkart.api.ideaboard.v1.core.WishListProductsWrapper;
import com.mebelkart.api.ideaboard.v1.mapper.WishListProductsMapper;
/**
 * @author Tinku
 *
 */
@UseStringTemplate3StatementLocator
public interface IdeaBoardDao {

	/**
	 * @param customerId
	 * @return
	 */
	@SqlQuery("SELECT id_wishlist, name, token, date_add, date_upd, counter FROM ps_wishlist "
			+ "WHERE id_customer = :cusId ORDER BY name ASC")
	@Mapper(WishListMapper.class)
	List<WishListWrapper> getWishListByCustId(@Bind("cusId") int customerId);

	/**
	 * @return
	 */
	@SqlQuery("SELECT SUM(wp.quantity) AS nbProducts, wp.id_wishlist "
			+ "FROM ps_wishlist_product wp "
			+ "INNER JOIN ps_wishlist w ON (w.id_wishlist = wp.id_wishlist) "
			+ "WHERE w.id_customer = :cusId "
			+ "GROUP BY w.id_wishlist "
			+ "ORDER BY w.name ASC")
	@Mapper(NBProductsMapper.class)
	List<NBProductsWrapper> getInfosByIdCustomer(@Bind("cusId") int cusId);

	/**
	 * @param wishListId
	 * @param customerId
	 * @return
	 */
	@SqlQuery("SELECT wp.id_product, wp.quantity, p.quantity AS product_quantity, pl.name, wp.id_product_attribute, "
			+ "wp.priority, pl.link_rewrite, cl.link_rewrite AS category_rewrite "
			+ "FROM ps_wishlist_product wp JOIN ps_product p ON p.id_product = wp.id_product "
			+ "JOIN ps_product_lang pl ON pl.id_product = wp.id_product "
			+ "JOIN ps_wishlist w ON w.id_wishlist = wp.id_wishlist "
			+ "LEFT JOIN ps_category_lang cl "
			+ "ON cl.id_category = p.id_category_default "
			+ "AND cl.id_lang = 1 "
			+ "WHERE w.id_customer = :cusId "
			+ "AND pl.id_lang = 1 "
			+ "AND wp.id_wishlist = :wishListId")
	@Mapper(WishListProductsMapper.class)
	List<WishListProductsWrapper> getProductsByIdCustomer(@Bind("wishListId") int wishListId,@Bind("cusId") int customerId);

	
}
