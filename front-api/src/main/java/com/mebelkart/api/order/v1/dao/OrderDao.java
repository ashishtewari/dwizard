package com.mebelkart.api.order.v1.dao;

import com.mebelkart.api.order.v1.core.Customer;
import com.mebelkart.api.order.v1.core.Order;
import com.mebelkart.api.order.v1.core.OrderDetail;
import com.mebelkart.api.order.v1.mapper.OrderDetailMapper;
import com.mebelkart.api.order.v1.mapper.OrderMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

import java.util.Date;
import java.util.List;

/**
 * Created by vinitpayal on 14/04/16.
 */
@UseStringTemplate3StatementLocator
public interface OrderDao {

    @Mapper(OrderMapper.class)
    @SqlQuery("select * "
            +" from ps_orders as o inner join ps_order_detail as od on o.id_order=od.id_order"
            +" left join ps_product as p on od.product_id=p.id_product "
            +" left join ps_order_detail_vendor_status as odvs on odvs.id_order_detail_vendor_status=od.id_order_detail"
            +" left join ps_order_detail_seller_statuses as odss on odss.id_order_detail_status= odvs.id_current_status"
            +" left join ps_customer as c on o.id_customer=c.id_customer"
            +" left join ps_address as da on o.id_address_delivery=da.id_address"
            +" left join ps_address as ia on o.id_address_invoice=ia.id_address"
            +" left join ps_country as dc on da.id_country=dc.id_country"
            +" left join ps_state as ds on da.id_state=ds.id_state"
            +" left join ps_country as ic on ia.id_country=ic.id_country"
            +" left join ps_state as ist on ia.id_state=ist.id_state"
            +" where <whereQuery>"
            +" LIMIT 20 offset :offset")
    List<Order> getAllOrders(@Define("whereQuery") String whereQuery, @Bind("fromDate") Date fromDate, @Bind("toDate") Date toDate, @Bind("statusRequired") String statusRequired, @Bind("orderId") Integer orderId, @Bind("offset") Integer offset);


    @SqlQuery("select count(o.id_order)"
            +" from ps_orders as o inner join ps_order_detail as od on o.id_order=od.id_order"
            +" left join ps_product as p on od.product_id=p.id_product "
            +" left join ps_order_detail_vendor_status as odvs on odvs.id_order_detail_vendor_status=od.id_order_detail"
            +" left join ps_order_detail_seller_statuses as odss on odss.id_order_detail_status= odvs.id_current_status"
            +" where <whereQuery>")
    Integer getOrderCount(@Define("whereQuery") String whereQuery,@Bind("fromDate") Date fromDate,@Bind("toDate") Date toDate,@Bind("statusRequired") String statusRequired,@Bind("orderId") Integer orderId);

    @Mapper(OrderDetailMapper.class)
    @SqlQuery("select * from ps_order_detail where id_order= :currentOrderId")
    List<OrderDetail> getSuborderDetail(@Bind("currentOrderId") Integer currentOrderId);


}


