package com.mebelkart.api.util.cronTasks.dao;

import java.sql.*;

/**
 * Created by vinitpayal on 29/03/16.
 */
public class OrderDao {

    private Connection sqlConnection;
    public OrderDao() throws SQLException {

            sqlConnection=DriverManager.getConnection("jdbc:mysql://localhost:3306/mebelkart_prod", "root", "root");
    }

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public void setSqlConnection(Connection sqlConnection) {
        this.sqlConnection = sqlConnection;
    }

    /**
     * This function will fetch all new order created in last 24 hours
     * @return Resultset containing all orders or null if any SqlException occurs
     */
    public ResultSet getNewOrders() throws SQLException {
        Statement getOrdersStatement = sqlConnection.createStatement();;
        String query = "SELECT * FROM ps_orders limit 50 offset 15000";
        ResultSet allNewOrdersResultSet = getOrdersStatement.executeQuery(query);

        return allNewOrdersResultSet;

    }

    /**
     * This function will fetch all suborders from ps_order_detail corresponding to a order id from  and
     * will find the status of all suborders from ps_order_detail_vendor_status.
     * @return
     */
    public ResultSet getOrderDetails(Integer orderId) throws SQLException {
        Statement orderDetailStatement=sqlConnection.createStatement();
        String queryToFetchAllSubordersAndStatus="select * from ps_order_detail as od left join ps_order_detail_vendor_status as odvs on odvs.id_order_detail_vendor_status=od.id_order_detail ";
        queryToFetchAllSubordersAndStatus+="where id_order= "+orderId;

        ResultSet subOrdersWithStatus=orderDetailStatement.executeQuery(queryToFetchAllSubordersAndStatus);
        return subOrdersWithStatus;
    }

    /**
     *
     * will accept a suborderId and will return logisticDetails for that suborder
     *
     * @param subOrderId
     * @return resultset
     * @throws SQLException
     */

    public ResultSet getLogisticDetailsForASuborder(Integer subOrderId) throws SQLException {
        Statement logisticDetailsStatement=sqlConnection.createStatement();
        String queryToFetchLogisticDetailsForASuborder="select * from ps_order_detail_logistic_service where id_order_detail="+subOrderId;
        ResultSet logisticDetailsResultSet=logisticDetailsStatement.executeQuery(queryToFetchLogisticDetailsForASuborder);
        return logisticDetailsResultSet;
    }

    /**
     * this function will return shipment details of a subOrder
     * @param subOrderId
     * @return
     * @throws SQLException
     */
    public ResultSet getSuborderShipmentDetails(Integer subOrderId) throws SQLException {
        Statement shipmentDetailsStatement=sqlConnection.createStatement();
        String subOrderDetailsShipments="select * from ps_order_detail_shipments where id_order_detail="+subOrderId;
        ResultSet subOrderDetailsShipmentResultset=shipmentDetailsStatement.executeQuery(subOrderDetailsShipments);
        return subOrderDetailsShipmentResultset;
    }

    /**
     * will return if an order is assigned to other vendor
     * @param subOrderId
     * @return
     * @throws SQLException
     */

    public ResultSet getIfSuborderIsAssignedToOtherVendor(Integer subOrderId) throws SQLException {
        Statement subOrderIsAssignedToOtherVendorStatement=sqlConnection.createStatement();
        String subOrderIsAssignedToOtherVendor="select * from ps_order_detail_assign_to_other_vendor where id_order_detail="+subOrderId;
        return subOrderIsAssignedToOtherVendorStatement.executeQuery(subOrderIsAssignedToOtherVendor);
    }

    /**
     *
     * @param subOrderId
     * @return
     * @throws SQLException
     */
    public ResultSet getPaymentInfoForSubOrder(Integer subOrderId) throws SQLException {
        Statement paymentInfoStatement=sqlConnection.createStatement();

        String paymentInfoQuery="select * from ps_order_detail_payment_info where id_order_detail="+subOrderId;
        return paymentInfoStatement.executeQuery(paymentInfoQuery);

    }


}
