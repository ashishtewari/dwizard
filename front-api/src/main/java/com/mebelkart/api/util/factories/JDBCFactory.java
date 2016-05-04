package com.mebelkart.api.util.factories;

import com.mebelkart.api.mkApiConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by vinitpayal on 03/05/16.
 */
/*
this factory will create an instance of sqlconnection for mebelkart_prod database
 */
public class JDBCFactory {
    private static Connection sqlConnection=null;
    private JDBCFactory(){};
    public static Connection getJDBCInstance() throws ClassNotFoundException, SQLException {
        if(sqlConnection==null)
        {
            Class.forName("com.mysql.jdbc.Driver");
            sqlConnection = DriverManager.getConnection(mkApiConfiguration.getMkProdDriverClass(), mkApiConfiguration.getMkProdUserName(), mkApiConfiguration.getMkProdPassword());
        }
        return sqlConnection;
    }


}
