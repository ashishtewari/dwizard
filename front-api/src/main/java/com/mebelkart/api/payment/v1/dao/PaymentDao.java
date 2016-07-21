/**
 * 
 */
package com.mebelkart.api.payment.v1.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @author Tinku
 *
 */
@UseStringTemplate3StatementLocator
public interface PaymentDao {

	/**
	 * @param cartId
	 * @return addressId 
	 */
	@SqlQuery("Select id_address_delivery from ps_cart where id_cart = :it")
	int getAddress(@Bind int cartId);

}
