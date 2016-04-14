package com.mebelkart.api.order.v1.dao;

import org.skife.jdbi.v2.sqlobject.SqlQuery;

import java.util.List;

/**
 * Created by vinitpayal on 14/04/16.
 */
public interface OrderDao {
    @SqlQuery("select id_order from ps_orders limit 100")
    List<Integer> getAllOrders();
}
