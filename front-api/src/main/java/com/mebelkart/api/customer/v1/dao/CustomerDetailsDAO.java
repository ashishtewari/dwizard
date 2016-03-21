/**
 * 
 */
package com.mebelkart.api.customer.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapperFactory;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.github.rkmk.mapper.CustomMapperFactory;
import com.github.rkmk.container.FoldingList;
import com.mebelkart.api.customer.v1.core.CustomerDetailsWrapper;

/**
 * @author Nikky-Akky return all details of a particular customer.
 *
 */
@UseStringTemplate3StatementLocator
public interface CustomerDetailsDAO {

	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT "
			+ "ps_customer.id_customer,ps_customer.firstname,ps_customer.lastname,ps_customer.email, "
			+ "ps_address.id_address AS address$id_address,ps_address.address1 AS address$address1,ps_address.address2 AS address$address2, "
			+ "ps_address.phone_mobile AS address$phone_mobile,ps_address.city AS address$city,ps_state.name AS address$name, "
			+ "ps_address.postcode AS address$postcode, ps_wishlist.id_wishlist AS wishlist$id_wishlist, "
			+ "ps_wishlist.name AS wishlist$name,ps_wishlist.date_add AS wishlist$date_add, "
			+ "ps_wishlist.date_upd AS wishlist$date_upd,ps_orders.id_order AS orders$id_order, "
			+ "ps_orders.total_products AS orders$total_products,ps_orders.total_paid AS orders$total_paid, "
			+ "ps_orders.total_discounts AS orders$total_discounts,ps_message.id_message AS messages$id_message, "
			+ "ps_message.message AS messages$message,ps_message.date_add AS messages$date_add "
			+ "FROM ps_customer "
			+ "LEFT JOIN ps_address ON ps_address.id_customer= ps_customer.id_customer "
			+ "LEFT JOIN ps_state ON ps_address.id_state=ps_state.id_state "
			+ "LEFT JOIN ps_wishlist ON ps_wishlist.id_customer=ps_customer.id_customer "
			+ "LEFT JOIN ps_orders ON ps_orders.id_customer=ps_customer.id_customer "
			+ "LEFT JOIN ps_message ON ps_message.id_customer=ps_customer.id_customer and ps_message.id_order=ps_orders.id_order "
			+ "WHERE ps_customer.id_customer= :customerId ORDER BY ps_message.id_message DESC")
	FoldingList<CustomerDetailsWrapper> getCustomerDetails(@Bind("customerId") long customerId);

	/**
	 * @param customerId,select query parameters and join query
	 * @return customer requested details
	 */
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT ps_customer.id_customer,<selectValue> FROM ps_customer <joinQuery> WHERE ps_customer.id_customer= :customerId <orderByQuery>")
	FoldingList<CustomerDetailsWrapper> getRequiredCustomerDetails(@Bind("customerId") long customerId,@Define("selectValue") String selectValue,@Define("joinQuery") String joinQuery,@Define("orderByQuery") String orderByQuery);

	/**
	 * @param customerId
	 * @return email of given customerId resulting that customerId is valid
	 */
	@RegisterMapperFactory(CustomMapperFactory.class)
	@SqlQuery("SELECT ps_customer.email from ps_customer where ps_customer.id_customer = :customerId")
	List<CustomerDetailsWrapper> getCustomerId(@Bind("customerId") long customerId);
	

}
