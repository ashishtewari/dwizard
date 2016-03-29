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
    public ResultSet getNewOrders(){
        Statement getOrdersStatement = null;
        try {
            getOrdersStatement = sqlConnection.createStatement();

            String query = "SELECT * FROM ps_orders limit 100 offset 500";
            ResultSet allNewOrdersResultSet = getOrdersStatement.executeQuery(query);

            return allNewOrdersResultSet;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }




}
