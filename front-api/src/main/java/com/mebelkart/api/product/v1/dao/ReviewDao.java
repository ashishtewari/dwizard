/**
 * 
 */
package com.mebelkart.api.product.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.product.v1.core.ProductReviewsWrapper;
import com.mebelkart.api.product.v1.mapper.ProductReviewsMapper;

/**
 * @author Tinku
 *
 */
@UseStringTemplate3StatementLocator
public interface ReviewDao {

	/**
	 * @param id
	 * @return
	 */
	@SqlQuery("SELECT "
			+ "re.a_reviewid,re.a_title, re.a_content, re.a_rating, re.a_permalink, "
			+ "date_format(re.a_adddate, \"%M %d, %Y\") a_adddate, re.a_orderid, cust.a_customername "
			+ "FROM "
			+ "t_review re "
			+ "join t_customer cust on cust.a_customerid = re.a_reviewer "
			+ "join t_product pro on pro.a_productid = re.a_productid "
			+ "WHERE "
			+ "re.a_isapproved = 1 "
			+ "and pro.a_productidentification = :productId "
			+ "order by re.a_adddate desc limit 0,100")
	@Mapper(ProductReviewsMapper.class)
	List<ProductReviewsWrapper> getProductReviews(@Bind("productId") String id);

	

}
