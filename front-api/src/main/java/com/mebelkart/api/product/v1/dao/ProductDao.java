package com.mebelkart.api.product.v1.dao;

import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import com.mebelkart.api.product.v1.core.TopProductsWrapper;
import com.mebelkart.api.product.v1.mapper.TopProductsMapper;

/**
 * Created by vinitpayal on 27/04/16.
 */
@UseStringTemplate3StatementLocator
public interface ProductDao {

    @SqlQuery("select value from ps_configuration where name= :configVarName")
    String getConfigVarValue(@Bind("configVarName") String configVarName);

    @SqlQuery("select name from ps_category_lang where id_category= :catId")
    String getNameOfCategory(@Bind("catId") String catId);
    
    @SqlQuery("select ps_order_detail.product_id, sum(ps_order_detail.product_quantity) as product_quantity "
    		+ "from ps_order_detail "
    		+ "INNER JOIN ps_orders "
    		+ "ON ps_orders.id_order = ps_order_detail.id_order "
    		+ "WHERE ps_orders.date_add BETWEEN <startDate> AND <endDate> "
    		+ "group by ps_order_detail.product_id "
    		+ "Order by product_quantity DESC "
    		+ "LIMIT :limit ")
    @Mapper(TopProductsMapper.class)
    List<TopProductsWrapper> getTopProducts(@Bind("limit") int limit, @Define("startDate") String startDate, @Define("endDate") String endDate);

}
