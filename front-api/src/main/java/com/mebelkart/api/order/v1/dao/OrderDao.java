package com.mebelkart.api.order.v1.dao;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by vinitpayal on 14/04/16.
 */
@UseStringTemplate3StatementLocator
public interface OrderDao {
    @SqlQuery("select o.id_order"
            +" from ps_orders as o inner join ps_order_detail as od on o.id_order=od.id_order"
            +" left join ps_product as p on od.product_id=p.id_product "
            +" left join ps_order_detail_vendor_status as odvs on odvs.id_order_detail_vendor_status=od.id_order_detail"
            +" left join ps_order_detail_seller_statuses as odss on odss.id_order_detail_status= odvs.id_current_status"
            +" where <whereQuery>")
    List<Integer> getAllOrders(@Define("whereQuery") String whereQuery,@Bind("fromDate") Date fromDate,@Bind("toDate") Date toDate,@Bind("statusRequired") String statusRequired);
}
