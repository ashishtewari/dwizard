
package com.mebelkart.api.rediscron.dao;

import java.sql.*;

/**
 * @author Tinku
 *
 */
public class JedisDao {

	private Connection sqlConnection;

	public JedisDao() throws ClassNotFoundException, SQLException  {		
		Class.forName("com.mysql.jdbc.Driver");
		sqlConnection = DriverManager.getConnection(com.mebelkart.api.mkApiConfiguration.getMkAuthDriverClass(), com.mebelkart.api.mkApiConfiguration.getMkAuthUserName(), com.mebelkart.api.mkApiConfiguration.getMkAuthPassword());
	}

	public Connection getSqlConnection() {
		return sqlConnection;
	}

	public void setSqlConnection(Connection sqlConnection) {
		this.sqlConnection = sqlConnection;
	}
	
	/**
	 * It retrieves all the wanted consumer details
	 * @return ResultSet of this query
	 * @throws SQLException
	 */
	public ResultSet getConsumerDetails() throws SQLException{
		Statement getConsumerDetails = sqlConnection.createStatement();
        String query = "SELECT id,a_user_name,a_access_token,a_count_assigned,a_is_active FROM mk_api_consumer WHERE a_redis_indexed = 0 OR a_changes_exist = 1";
        ResultSet allNewConsumerDetailResultSet = getConsumerDetails.executeQuery(query);

        return allNewConsumerDetailResultSet;
	}

	/**
	 * This function retrieves all resource ids with get permissions of single consumer
	 * @param consumerId
	 * @return ResultSet of this query
	 * @throws SQLException
	 */
	public ResultSet getResourcePermissionsDetails(int consumerId,String tableName, String colName) throws SQLException {
		Statement getResourcePermissions = sqlConnection.createStatement();
        String query = "SELECT a_resource_id,a_have_get_permission,a_have_post_permission,a_have_put_permission FROM "+tableName+" WHERE "+colName+" = "+consumerId;
        ResultSet allNewResourcePermissionResultSet = getResourcePermissions.executeQuery(query);

        return allNewResourcePermissionResultSet;
	}

	/**
	 * This function returns all the function names of this particular resource with respect to the consumer id provided
	 * @param consumerId
	 * @param resourceId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getFunctionPermissionsDetails(int consumerId, int resourceId, String method,String tableName,String colName) throws SQLException {		
		Statement getFunctionNames = sqlConnection.createStatement();
		String query = "SELECT mk_api_functions.a_function_name FROM mk_api_functions INNER JOIN "+tableName+" "
				+ "ON "
				+ tableName+".a_function_id = mk_api_functions.id "
				+ "WHERE "
				+ tableName+"."+colName+" = "+consumerId+" "
				+ "AND "
				+ "mk_api_functions.a_type = \""+method+"\" "
				+ "AND "
				+ tableName+".a_is_active = 1 "
				+ "AND "
				+ "mk_api_functions.a_resource_id = "+resourceId;
		ResultSet allNewFunctionPermissionResultSet = getFunctionNames.executeQuery(query);
		
		return allNewFunctionPermissionResultSet;
	}

	/**
	 * This function returns all the resource names based on resource id 
	 * @param resourceId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getResourceName(int resourceId) throws SQLException{
		Statement getResourceName = sqlConnection.createStatement();
		String query = "SELECT a_resource_name FROM mk_api_resources WHERE id = "+resourceId;
		ResultSet resourceName = getResourceName.executeQuery(query);
		
		return resourceName;
	}

	/**
	 * This function updates consumer table by setting redis indexed to 1 and changes exists to 0
	 * @param consumerId
	 * @throws SQLException
	 */
	public void updateConsumerRedisIndexedStatus(int consumerId,String tableName) throws SQLException {
		Statement updateUserRedisStatus = sqlConnection.createStatement();
		String query = "UPDATE "+tableName+" SET a_redis_indexed = 1, a_changes_exist = 0 where id = "+consumerId;
		updateUserRedisStatus.executeUpdate(query);
	}

	/**
	 * 
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getAdminDetails() throws SQLException {
		Statement getAdminDetails = sqlConnection.createStatement();
        String query = "SELECT id,a_user_name,a_access_token,a_admin_level,a_is_active FROM mk_api_user_admin WHERE a_redis_indexed = 0 OR a_changes_exist = 1";
        ResultSet allNewAdminDetailResultSet = getAdminDetails.executeQuery(query);

        return allNewAdminDetailResultSet;
	}

	/**
	 * @param adminId
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getResourcePermissionsDetailsOfAdmin(int adminId) throws SQLException {
		Statement getResourcePermissions = sqlConnection.createStatement();
        String query = "SELECT a_resource_id,a_have_get_permission,a_have_post_permission,a_have_put_permission FROM mk_api_resources_admin_permission WHERE a_admin_id = "+adminId;
        ResultSet allNewResourcePermissionResultSet = getResourcePermissions.executeQuery(query);

        return allNewResourcePermissionResultSet;
	}

	/**
	 * @param adminId
	 * @param resourceId
	 * @param methodName
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getFunctionPermissionsDetailsOfAdmin(int adminId,int resourceId, String methodName) throws SQLException {
		Statement getFunctionNames = sqlConnection.createStatement();
		String query = "SELECT mk_api_functions.a_function_name FROM mk_api_functions INNER JOIN mk_api_resources_admin_function_permission "
				+ "ON "
				+ "mk_api_resources_admin_function_permission.a_function_id = mk_api_functions.id "
				+ "WHERE "
				+ "mk_api_resources_admin_function_permission.a_admin_id = "+adminId+" "
				+ "AND "
				+ "mk_api_functions.a_type = \""+methodName+"\" "
				+ "AND "
				+ "mk_api_resources_admin_function_permission.a_is_active = 1 "
				+ "AND "
				+ "mk_api_functions.a_resource_id = "+resourceId;
		ResultSet allNewFunctionPermissionResultSet = getFunctionNames.executeQuery(query);
		
		return allNewFunctionPermissionResultSet;
	}

	/**
	 * @param adminId
	 * @throws SQLException
	 */
	public void updateAdminRedisIndexedStatus(int adminId) throws SQLException {
		Statement updateUserRedisStatus = sqlConnection.createStatement();
		String query = "UPDATE mk_api_user_admin SET a_redis_indexed = 1, a_changes_exist = 0 where id = "+adminId;
		updateUserRedisStatus.executeUpdate(query);
	}
	
	/**
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException{
		sqlConnection.close();
	}

	/**
	 * @param consumerId
	 * @param dayCount
	 * @param previousDate
	 * @throws SQLException
	 */
	public void updateHitsAndDate(int consumerId,int dayCount, String previousDate) throws SQLException {
		Statement updateUserRedisStatus = sqlConnection.createStatement();
		String query = "INSERT INTO mk_api_statics (a_consumer_id,a_date,a_hits) VALUES ("+consumerId+",\""+previousDate+"\","+dayCount+")";
		updateUserRedisStatus.executeUpdate(query);
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getConsumerId(String userName) throws SQLException {
		Statement getConsumerId = sqlConnection.createStatement();
        String query = "SELECT id FROM mk_api_consumer WHERE a_user_name = \""+userName+"\"";
        ResultSet consumerIdsResultSet = getConsumerId.executeQuery(query);

        return consumerIdsResultSet;
	}
	
	/**
	 * It retrieves the active status of consumer
	 * @return ResultSet of this query
	 * @throws SQLException
	 */
	public ResultSet getConsumerStatus(int consumerId) throws SQLException{
		Statement getConsumerStatus = sqlConnection.createStatement();
        String query = "SELECT a_user_name,a_is_active FROM mk_api_consumer WHERE id = "+consumerId;
        ResultSet consumerStatusResultSet = getConsumerStatus.executeQuery(query);

        return consumerStatusResultSet;
	}

	public ResultSet getAdminId(String userName) throws SQLException {
		Statement getAdminId = sqlConnection.createStatement();
	    String query = "SELECT id FROM mk_api_user_admin WHERE a_user_name = \""+userName+"\"";
	    ResultSet adminIdsResultSet = getAdminId.executeQuery(query);
	
	    return adminIdsResultSet;
	}

	public ResultSet getAdminStatus(int adminId) throws SQLException{
		Statement getAdminStatus = sqlConnection.createStatement();
        String query = "SELECT a_user_name,a_is_active FROM mk_api_user_admin WHERE id = "+adminId;
        ResultSet adminStatusResultSet = getAdminStatus.executeQuery(query);

        return adminStatusResultSet;
	}
}
