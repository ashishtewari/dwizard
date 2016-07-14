/**
 * 
 */
package com.mebelkart.api.ideaboard.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.ideaboard.v1.core.AttributeWrapper;
import com.mebelkart.api.ideaboard.v1.core.WishListWrapper;
import com.mebelkart.api.ideaboard.v1.core.NBProductsWrapper;
import com.mebelkart.api.ideaboard.v1.mapper.AtrributeMapper;
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
			+ "LEFT JOIN ps_category_lang cl ON cl.id_category = p.id_category_default AND cl.id_lang = 1 "
			+ "WHERE w.id_customer = :cusId "
			+ "AND pl.id_lang = 1 "
			+ "AND wp.id_wishlist = :wishListId")
	@Mapper(WishListProductsMapper.class)
	List<WishListProductsWrapper> getProductsByIdCustomer(@Bind("wishListId") int wishListId,@Bind("cusId") int customerId);

	/**
	 * @param productAttributeId
	 * @return
	 */
	@SqlQuery("SELECT al.name AS attribute_name, pa.quantity AS attribute_quantity "
			+ "FROM ps_product_attribute_combination pac "
			+ "LEFT JOIN ps_attribute a ON (a.id_attribute = pac.id_attribute) "
			+ "LEFT JOIN ps_attribute_group ag ON (ag.id_attribute_group = a.id_attribute_group) "
			+ "LEFT JOIN ps_attribute_lang al ON (a.id_attribute = al.id_attribute AND al.id_lang = 1) "
			+ "LEFT JOIN ps_attribute_group_lang agl ON (ag.id_attribute_group = agl.id_attribute_group AND agl.id_lang = 1) "
			+ "LEFT JOIN ps_product_attribute pa ON (pac.id_product_attribute = pa.id_product_attribute) "
			+ "WHERE pac.id_product_attribute = :proAttrId")
	@Mapper(AtrributeMapper.class)
	List<AttributeWrapper> getAttributes(@Bind("proAttrId") int productAttributeId);

	/**
	 * @param customerId
	 * @param ideaBoardName
	 * @return
	 */
	@SqlQuery("select count(*) as total from ps_wishlist where name = :name and id_customer = :cusId")
	Integer isExistsByNameForUser(@Bind("cusId") int customerId, @Bind("name") String ideaBoardName);

	/**
	 * @param customerId
	 * @param token
	 * @param ideaBoardName
	 * @param counter
	 * @param currentDateString
	 * @param currentDateString2
	 */
	@SqlUpdate("insert into ps_wishlist (id_customer,token,name,counter,date_add,date_upd) values (:cusId,:token,:name,:counter,:dateAdd,:dateUpd)")
	void createNewIB(@Bind("cusId")int customerId,@Bind("token") String token,@Bind("name") String ideaBoardName,
			@Bind("counter") String counter,@Bind("dateAdd") String currentDate,@Bind("dateUpd") String updDate);

	/**
	 * @param customerId
	 * @param customerId2 
	 * @param token
	 * @param ideaBoardName
	 * @param counter
	 * @param currentDateString
	 */
	@SqlUpdate("update ps_wishlist set id_customer = :cusId,token = :token,"
			+ "name = :name, counter = :counter,date_upd = :dateUpd where id_wishlist = :wishlistId")
	void updateIB(@Bind("wishlistId")int wishlistId,@Bind("cusId")int customerId,@Bind("token") String token,
			@Bind("name") String ideaBoardName,@Bind("counter")String counter,@Bind("dateUpd") String updDate);
	
	/**
	 * @param wishlistId
	 * @param customerId
	 * @param prodId
	 * @param prodAttrId
	 * @return
	 */
	@SqlQuery("select wp.quantity from ps_wishlist_product wp join ps_wishlist w on (w.id_wishlist = wp.id_wishlist)"
			+ " where wp.id_wishlist = :wishListId"
			+ " and w.id_customer = :cusId"
			+ " and wp.id_product = :prodId"
			+ " and wp.id_product_attribute = :prodAttrId")
	List<Integer> checkIfItemExists(@Bind("wishListId") int wishlistId,@Bind("cusId") int customerId,
			@Bind("prodId")int prodId,@Bind("prodAttrId")int prodAttrId);

	/**
	 * @param wishlistId
	 * @param prodId
	 * @param prodAttrId
	 * @param quantity
	 */
	@SqlUpdate("insert into ps_wishlist_product (id_wishlist,id_product,id_product_attribute,quantity,priority) "
			+ "values (:wishListId, :prodId, :prodAttrId, :quantity, 1)")
	@GetGeneratedKeys
	Integer addWishBoard(@Bind("wishListId")int wishlistId,@Bind("prodId")int prodId,@Bind("prodAttrId")int prodAttrId,
			@Bind("quantity") int quantity);

	/**
	 * @param customerId
	 * @param wishlistId
	 * @return
	 */
	@SqlQuery("select id_wishlist from ps_wishlist where id_customer = :cusId and id_wishlist = :wishlistId")
	int isValidWishlistIdWithRespectiveToCustId(@Bind("cusId") int customerId,@Bind("wishlistId") int wishlistId);

	/**
	 * @param wishlistId
	 */
	@SqlUpdate("delete from ps_wishlist where id_wishlist = :it")
	void deleteFromWishlist(@Bind int wishlistId);

	/**
	 * @param wishlistId
	 */
	@SqlUpdate("delete from ps_wishlist_email where id_wishlist = :it")
	void deleteFromWishlistEmail(@Bind int wishlistId);

	/**
	 * @param wishlistId
	 */
	@SqlUpdate("delete from ps_wishlist_product where id_wishlist = :it")
	void deleteFromWishlistProduct(@Bind int wishlistId);
	
}
