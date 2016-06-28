/**
 * 
 */
package com.mebelkart.api.cart.v1.dao;



import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;

import com.github.rkmk.container.FoldingList;
import com.github.rkmk.mapper.CustomMapperFactory;
import com.mebelkart.api.cart.v1.core.CartProductDetailsWrapper;

/**
 * @author Nikhil
 *
 */
public interface CartOperationsDAO {

	/**
	 * @param cartId
	 * @param productId
	 * @param productAttributeId
	 * @return 
	 */
	@SqlUpdate("Insert into ps_cart_product(id_cart,id_product,id_product_attribute,quantity,date_add) "
			+ "VALUES(:cartId,:productId,:productAttributeId,1,CURRENT_TIMESTAMP)")
	int addProductToExistingCart(@Bind("cartId")String cartId, @Bind("productId")String productId, @Bind("productAttributeId")String productAttributeId);

	/**
	 * @param cartId
	 * @param productId
	 * @return
	 */
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("select id_cart,id_product,id_product_attribute,quantity from ps_cart_product "
			+ "where id_cart=:cartId and id_product=:productId")
	FoldingList<CartProductDetailsWrapper> getCartProductDetails(@Bind("cartId")String cartId,@Bind("productId")String productId);

	/**
	 * @param productId
	 * @return the quantity of product
	 */
	@SqlQuery("select quantity from ps_cart_product where id_cart=:cartId and id_product=:productId")
	Integer getProductQuantity(@Bind("cartId")String cartId,@Bind("productId")String productId);

	/**
	 * @param cartId
	 * @return totalCartQuantity
	 */
	@SqlQuery("select sum(quantity) from ps_cart_product where id_cart=:cartId")
	Integer getCartTotalQuantity(@Bind("cartId")String cartId);

	/**
	 * @param cartId
	 * @return
	 */
	@SqlQuery("SELECT id_product FROM ps_cart_product WHERE id_cart=:cartId")
	List<Integer> getAllProductsInCart(@Bind("cartId")String cartId);

	/**
	 * @param cartId
	 * @return
	 */
	@SqlQuery("SELECT count(id_product) FROM ps_cart_product WHERE id_cart=:cartId")
	Integer getCartQuantity(@Bind("cartId")String cartId);

	/**
	 * @param cartId
	 * @param productId
	 * @return
	 */
	@SqlUpdate("update ps_cart_product set quantity=quantity+1 where id_cart=:cartId and id_product=:productId")
	Integer updateProductQuantityInCart(@Bind("cartId")String cartId, @Bind("productId")String productId);

	/**
	 * @param i
	 * @return
	 */
	@SqlQuery("select count(id_product) from ps_product where id_product=:productId")
	Integer isProductIdValid(@Bind("productId")String productId);

	/**
	 * @param i
	 * @return
	 */
	@SqlQuery("select count(id_cart) from ps_cart where id_cart=:cartId")
	Integer isCartIdValid(@Bind("cartId")String cartId);

	/**
	 * @param productId
	 * @param langId
	 * @param currencyId
	 * @param carrierId 
	 * @param customerId
	 * @param guestId
	 * @param secureKey
	 * @return
	 */
	@SqlUpdate("Insert into ps_cart(id_carrier,id_lang,id_address_delivery,id_address_invoice,id_currency,"
			+ "id_customer,id_guest,secure_key,recyclable,gift,gift_message,date_add,date_upd) "
			+ "VALUES(:carrierId,:langId,0,0,:currencyId,:customerId,:guestId,:secureKey,1,0,'',"
			+ "CURRENT_TIMESTAMP,CURRENT_TIMESTAMP)")
	@GetGeneratedKeys
	Integer createCart(@Bind("langId")int langId, @Bind("currencyId")int currencyId,
			@Bind("carrierId")int carrierId, @Bind("customerId")String customerId, 
			@Bind("guestId")String guestId, @Bind("secureKey")String secureKey);
	
	
}
