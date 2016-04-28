/**
 * 
 */
package com.mebelkart.api.util.cronTasks.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.elasticsearch.client.Client;

import com.mebelkart.api.util.factories.ElasticFactory;



/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerDao {

	private Connection sqlConnection;
	Client client = ElasticFactory.getElasticClient();
	public Connection getSqlConnection() {
		return sqlConnection;
	}

	public void setSqlConnection(Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}
	
	public ManufacturerDao() throws SQLException{
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mebelkart_prod", "root", "root");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet getManufacturerDetails() throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select * from ps_manufacturer";
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerAddressDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "SELECT * FROM `ps_address` RIGHT JOIN ps_manufacturer_addresses "
				+ "ON ps_address.id_address=ps_manufacturer_addresses.id_address "
				+ "WHERE ps_manufacturer_addresses.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerCompanyInfoDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select * from ps_manufacturer_company_info where ps_manufacturer_company_info.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerProductDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select ps_product.id_product,ps_product.id_category_default,ps_product.price,ps_product_lang.name,ps_product.id_manufacturer "
				+ "from ps_product JOIN ps_product_lang ON ps_product_lang.id_product=ps_product.id_product "
				+ "where ps_product.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getOrderDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "SELECT DISTINCT ps_order_detail.id_order,ps_orders.date_add,ps_product.id_manufacturer from  ps_order_detail "
				+ "JOIN ps_product ON ps_order_detail.product_id=ps_product.id_product "
				+ "JOIN ps_orders ON ps_order_detail.id_order=ps_orders.id_order "
				+ "where ps_product.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
}