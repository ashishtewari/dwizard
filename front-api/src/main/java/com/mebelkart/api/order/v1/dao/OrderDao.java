package com.mebelkart.api.order.v1.dao;


import com.mebelkart.api.order.v1.core.Order;
import com.mebelkart.api.order.v1.core.OrderDetail;
import com.mebelkart.api.order.v1.core.OrderDetailSellerStatuses;
import com.mebelkart.api.order.v1.mapper.OrderDetailMapper;
import com.mebelkart.api.order.v1.mapper.OrderDetailSellerStatusesMapper;
import com.mebelkart.api.order.v1.mapper.OrderMapper;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
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
    @SqlQuery("select DISTINCT(o.id_order),o.*,p.*,c.*,da.*,ia.*,dc.*,ic.*,ds.*,ist.* "
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
            +" where 1=1 <whereQuery>"
            +" LIMIT 20 offset :offset")
    List<Order> getAllOrders(@Define("whereQuery") String whereQuery, @Bind("fromDate") Date fromDate, @Bind("toDate") Date toDate, @Bind("statusRequired") String statusRequired, @Bind("orderId") Integer orderId, @Bind("offset") Integer offset);


    @SqlQuery("select count(DISTINCT(o.id_order))"
            +" from ps_orders as o inner join ps_order_detail as od on o.id_order=od.id_order"
            +" left join ps_product as p on od.product_id=p.id_product "
            +" left join ps_order_detail_vendor_status as odvs on odvs.id_order_detail_vendor_status=od.id_order_detail"
            +" left join ps_order_detail_seller_statuses as odss on odss.id_order_detail_status= odvs.id_current_status"
            +" where 1=1 <whereQuery>")
    Integer getOrderCount(@Define("whereQuery") String whereQuery,@Bind("fromDate") Date fromDate,@Bind("toDate") Date toDate,@Bind("statusRequired") String statusRequired,@Bind("orderId") Integer orderId);

    @Mapper(OrderDetailMapper.class)
    @SqlQuery("select * from ps_order_detail as od"
            + " left join ps_order_detail_vendor_status as odvs on od.id_order_detail=odvs.id_order_detail_vendor_status"
            + " left join ps_order_detail_seller_statuses as odss on odvs.id_current_status=odss.id_order_detail_status"
            + " where id_order= :currentOrderId"
    )
    List<OrderDetail> getSuborderDetail(@Bind("currentOrderId") Integer currentOrderId);


	/**
	 * This function returns all_status_info details of particular subOrderId
	 * @param subOrderId
	 * @return
	 */
    @SqlQuery("select all_status_info from ps_order_detail_vendor_status where id_order_detail_vendor_status = :subOrderId")
	String getSubOrderStatusInfo(@Bind("subOrderId") String subOrderId);


	/**
	 * This function checks whether suborderid exists or not
	 * @param subOrderId
	 * @return
	 */
    @SqlQuery("select id_order_detail_vendor_status from ps_order_detail_vendor_status where id_order_detail_vendor_status = :subOrderId")
    Integer isSubOrderExists(@Bind("subOrderId") String subOrderId);

	/**
	 * This function returns the vendors display status
	 * @param updatedOrderStatus
	 * @return
	 */
    @Mapper(OrderDetailSellerStatusesMapper.class)
    @SqlQuery("select display_to_seller,status_name,active from ps_order_detail_seller_statuses where id_order_detail_status = :orderStatus")
	OrderDetailSellerStatuses getOrderDetailsSellerStatuses(@Bind("orderStatus") String updatedOrderStatus);


	/**
	 * This function will create a new suborder
	 * @param subOrderId
	 * @param statusInfo
	 * @param updatedOrderStatus
	 */
    @SqlUpdate("insert into ps_order_detail_vendor_status (id_order_detail_vendor_status,all_status_info,id_current_status,last_status_updated_on) "
    		+ "values (:subOrderId, :statusInfo, :updatedOrderStatus, :currentDateTime)")
	void createSubOrder(@Bind("subOrderId") String subOrderId, @Bind("statusInfo")String statusInfo,
			@Bind("updatedOrderStatus") String updatedOrderStatus,@Bind("currentDateTime") String currentDateTime);


	/**
	 * This function will update exicting suborder status
	 * @param subOrderId
	 * @param statusInfo
	 * @param updatedOrderStatus
	 */
    @SqlUpdate("update ps_order_detail_vendor_status set all_status_info = :statusInfo,"
    		+ "id_current_status = :updatedOrderStatus, last_status_updated_on = :currentDateTime "
    		+ "where id_order_detail_vendor_status = :subOrderId")
	void updateSubOrder(@Bind("subOrderId") String subOrderId, @Bind("statusInfo") String statusInfo,
			@Bind("updatedOrderStatus") String updatedOrderStatus,@Bind("currentDateTime") String currentDateTime);

}

