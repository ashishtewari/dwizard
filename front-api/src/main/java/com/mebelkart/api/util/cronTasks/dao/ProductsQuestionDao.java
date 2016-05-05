package com.mebelkart.api.util.cronTasks.dao;

import com.mebelkart.api.util.factories.JDBCFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vinitpayal on 03/05/16.
 */
public class ProductsQuestionDao {
    private Connection sqlConnection;
    public ProductsQuestionDao() throws SQLException, ClassNotFoundException {
        System.out.println();
        sqlConnection= JDBCFactory.getJDBCInstance();
    }
    public ResultSet getProductsQuestion() throws SQLException {
        Statement getProductQueStatement = sqlConnection.createStatement();;
        String query = "SELECT * FROM ps_qna limit 10";
        ResultSet allProductQue=getProductQueStatement.executeQuery(query);
        return allProductQue;        
    }   
}