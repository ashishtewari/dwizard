/**
 * 
 */
package com.mebelkart.api.util.cronTasks.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import com.mebelkart.api.util.factories.ElasticFactory;

/**
 * @author Nikky-Akky
 *
 */
public class ManufacturerDao {

	private Connection sqlConnection;
	@SuppressWarnings("static-access")
	Client client = ElasticFactory.getElasticClient();
	public Connection getSqlConnection() {
		return sqlConnection;
	}

	public void setSqlConnection(Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}
	
	public ManufacturerDao() throws SQLException{
		sqlConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mebelkart_prod", "root", "root");
	}
	
	public ResultSet getManufacturerDetails() throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select * from ps_manufacturer where id_manufacturer=192";
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerAddressDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "SELECT * FROM `ps_address` RIGHT JOIN ps_manufacturer_addresses ON ps_address.id_address=ps_manufacturer_addresses.id_address WHERE ps_manufacturer_addresses.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerCompanyInfoDetails(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select * from ps_manufacturer_company_info where ps_manufacturer_company_info.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getManufacturerProductId(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "select ps_product.id_product from ps_product where ps_product.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public ResultSet getProductOrderId(Integer manufacturerId) throws SQLException{
		Statement manufacturerDetailsStatement = sqlConnection.createStatement();
		String query = "SELECT ps_order_detail.id_order from  ps_order_detail JOIN ps_product ON "
				+ "ps_order_detail.product_id=ps_product.id_product "
				+ "where ps_product.id_manufacturer="+manufacturerId;
		ResultSet manufacturerDetailsResultSet = manufacturerDetailsStatement.executeQuery(query);
		return manufacturerDetailsResultSet;
		
	}
	
	public SearchResponse getManufacturerOrderDetails(Integer orderId) throws SQLException{
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.should(QueryBuilders.termsQuery("_id", orderId+""));
		SearchResponse response = client.prepareSearch("mk").setTypes("order")
                .setQuery(query).execute()
                .actionGet();
		return response;
		
	}
}
