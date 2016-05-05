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
    private static Connection sqlConnection;
    private JDBCFactory(){};
    public static Connection getJDBCInstance() throws ClassNotFoundException, SQLException {
        System.out.println("-------------------------------");
        System.out.println("JDBC Factory called");
        System.out.println("-------------------------------");

        if(sqlConnection==null)
        {
            System.out.println("-------------------------------");
            System.out.println("JDBC connection created");
            System.out.println("-------------------------------");
            Class.forName("com.mysql.jdbc.Driver");
            sqlConnection = DriverManager.getConnection(mkApiConfiguration.getMkProdDriverClass(), mkApiConfiguration.getMkProdUserName(), mkApiConfiguration.getMkProdPassword());
        }
        return sqlConnection;
    }


}
