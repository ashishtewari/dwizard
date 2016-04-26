package com.mebelkart.api.order.v1.mapper;

import com.mebelkart.api.order.v1.core.OrderDetail;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vinitpayal on 21/04/16.
 */
public class OrderDetailMapper implements ResultSetMapper<OrderDetail> {
    @Override
    public OrderDetail map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException {
        return new OrderDetail(resultSet.getInt("id_order_detail"),resultSet.getInt("id_order"),resultSet.getInt("product_id")
         ,resultSet.getInt("product_attribute_id"),resultSet.getString("product_name"),resultSet.getInt("product_quantity")
         ,resultSet.getInt("product_quantity_in_stock"),resultSet.getInt("product_quantity_refunded"),resultSet.getInt("product_quantity_reinjected")
         ,resultSet.getFloat("order_detail_wholesale_price"),resultSet.getFloat("product_price"),resultSet.getFloat("reduction_percent")
         ,resultSet.getFloat("reduction_amount"),resultSet.getFloat("group_reduction"),resultSet.getFloat("product_quantity_discount")
         ,resultSet.getFloat("order_detail_product_advance_amount"),resultSet.getInt("shipped_from_date"),resultSet.getInt("shipped_to_date")
         ,resultSet.getInt("delivered_from_date"),resultSet.getInt("delivered_to_date"),resultSet.getString("product_ean13")
         ,resultSet.getString("product_upc"),resultSet.getString("product_reference"),resultSet.getString("product_supplier_reference")
         ,resultSet.getFloat("product_weight"),resultSet.getString("tax_name"),resultSet.getFloat("tax_rate"),resultSet.getFloat("ecotax"),resultSet.getFloat("ecotax_tax_rate")
         ,resultSet.getBoolean("discount_quantity_applied"),resultSet.getString("download_hash"),resultSet.getInt("download_nb"),resultSet.getString("download_deadline"));
    }
}
