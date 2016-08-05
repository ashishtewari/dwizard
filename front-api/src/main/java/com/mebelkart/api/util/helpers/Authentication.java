package com.mebelkart.api.util.helpers;

import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

import com.mebelkart.api.util.factories.JDBCFactory;
import com.mebelkart.api.util.factories.JedisFactory;

public class Authentication {
	/**
	 * Getting redis client
	 */
	JedisFactory jedisAuthentication = new JedisFactory();
	/**
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(Authentication.class);
	Connection sqlConnection ;

	public Authentication() {
	}
	
	public void validate(String user, String apikey, String resourceName, String method, String functionName) throws Exception {
		try {
			// Inside, It checks whether the user is super admin or not, If he is super admin then we gonna give him access permission else we gonna authenticate him
			jedisAuthentication.validate(user,apikey, resourceName, method,functionName);
		} catch (Exception e) {	
			//if(e.getMessage().equals("java.net.SocketException: Connection reset by peer: socket write error") || e.getMessage().equals("Could not get a resource from the pool")|| e.getMessage().equals("ERR Client sent AUTH, but no password is set") || e.getMessage().equals("ERR invalid password")){
			if(e instanceof JedisException || e instanceof JedisConnectionException || e instanceof JedisDataException || e instanceof SocketTimeoutException){
				log.info("Redis server responded with "+e.getMessage());
				try{
					sqlValidate(user, apikey, resourceName, method,functionName);
				}catch(Exception ex){
					throw new Exception(ex.getMessage());
				}
			}else{
				throw new Exception(e.getMessage());
			}
		}	
	}

	private void sqlValidate(String user, String apikey, String resourceName,String method, String functionName) throws Exception{
		try{
			if(user.equals("") || user == null){
				throw new Exception("Your user name is empty");
			}else if(apikey.equals("") || apikey == null){
				throw new Exception("Your access token is empty");
			}
			String userType = getUserType(user);
			// checks if the userName exists in SQL or not
			if (userType != null) {
				// checks whether the user is super admin or not, If he is super admin then we gonna give him access permission
				if(userType.equals("admin")){
					if(isSuperAdmin(user,apikey) == 1){
						return;
					}
				}
				// checks if this particular user is having valid apikey or not and also checks whether the user is in active state or not
				long userId = isHavingValidAccessTokenAndActive(user,apikey,userType);
				if(userId != 0){
					// checks if he had access to that particular resource and checks if he had access to that particular method
					if (containsResourceAndMethodPermissions(userId, resourceName,method,userType)) {
						// checks if he had access to this particular function
						if(containsFunction(userId,resourceName,method,functionName,userType)){
							// Api Permission Granted
						}else{
							// User doesn't have access to this function
							throw new Exception("You don't have access to this function");
						}
					} else {
						// User doesn't have Access to this Resource
						throw new Exception("You don't have Access to this Resource/Method");
					}
				}else{
					// Not a Valid Access Token
					throw new Exception("You don't have Valid Access Token/In-Active");
				}
			} else {
				// Not a Valid User
				throw new Exception("you are not a Valid User");
			}
		}catch(Exception e){
			throw new Exception(e.getMessage());
		}		
	}

	/**
	 * Checks whether the user is super admin or not
	 * @param user
	 * @param apikey
	 * @return
	 */
	private int isSuperAdmin(String user, String apikey) throws ClassNotFoundException, SQLException{
		String query = "";
		sqlConnection = JDBCFactory.getMkAuthJDBCInstance();
		Statement getUserDetails = sqlConnection.createStatement();
		query = "SELECT a_admin_level FROM mk_api_user_admin WHERE a_user_name = \""+user+"\" AND a_access_token = \""+apikey+"\" AND a_is_active = 1";
        ResultSet userDetailsResultSet = getUserDetails.executeQuery(query);
        while(userDetailsResultSet.next()){
           	return userDetailsResultSet.getInt("a_admin_level");
        }
        sqlConnection.close();
		return 0;
	}

	private boolean containsFunction(long userId,String resourceName, String method,
			String functionName, String userType) throws ClassNotFoundException, SQLException{
		String query = "";
		sqlConnection = JDBCFactory.getMkAuthJDBCInstance();
		Statement getUserDetails = sqlConnection.createStatement();
		if(userType.equals("admin")){
			query = "SELECT id FROM mk_api_resources_admin_function_permission "
					+ "WHERE a_admin_id = \""+userId+"\" "
							+ "AND a_function_id "
							+ "IN (SELECT id FROM mk_api_functions WHERE a_resource_id "
							+ "IN (SELECT id FROM mk_api_resources WHERE a_resource_name = \""+resourceName+"\") "
									+ "AND a_function_name = \""+functionName+"\" AND a_type = \""+method.toLowerCase()+"\")";
		}else{
			query = "SELECT id FROM mk_api_resources_consumer_function_permission "
					+ "WHERE a_consumer_id = \""+userId+"\" "
					+ "AND a_function_id "
					+ "IN (SELECT id FROM mk_api_functions WHERE a_resource_id "
					+ "IN (SELECT id FROM mk_api_resources WHERE a_resource_name = \""+resourceName+"\") "
							+ "AND a_function_name = \""+functionName+"\" AND a_type = \""+method.toLowerCase()+"\")";
		}
        ResultSet userDetailsResultSet = getUserDetails.executeQuery(query);
        while(userDetailsResultSet.next()){
        	if(userDetailsResultSet.getInt("id") != 0){
        		return true;
        	}
        }
        sqlConnection.close();
        return false;
	}

	private boolean containsResourceAndMethodPermissions(long userId,
			String resourceName, String method, String userType) throws ClassNotFoundException, SQLException{
		String query = "";
		String methodName = "";
		if(method.equalsIgnoreCase("get"))
			methodName = "a_have_get_permission";
		else if(method.equalsIgnoreCase("put"))
			methodName = "a_have_put_permission";
		else if(method.equalsIgnoreCase("post"))
			methodName = "a_have_post_permission";
		sqlConnection = JDBCFactory.getMkAuthJDBCInstance();
		Statement getUserDetails = sqlConnection.createStatement();
		if(userType.equals("admin")){
			query = "SELECT a_permission_id FROM mk_api_resources_admin_permission "
					+ "WHERE a_admin_id = \""+userId+"\" AND "+methodName+" = 1 AND "
							+ "a_resource_id "
							+ "IN (SELECT id FROM mk_api_resources WHERE a_resource_name = \""+resourceName+"\")";
		}else{
			query = "SELECT a_permission_id FROM mk_api_resources_consumer_permission "
					+ "WHERE a_consumer_id = \""+userId+"\" AND "+methodName+" = 1 AND "
							+ "a_resource_id "
							+ "IN (SELECT id FROM mk_api_resources WHERE a_resource_name = \""+resourceName+"\")";
		}
        ResultSet userDetailsResultSet = getUserDetails.executeQuery(query);
        while(userDetailsResultSet.next()){
        	if(userDetailsResultSet.getInt("a_permission_id") != 0){
        		return true;
        	}
        }
        sqlConnection.close();
        return false;
	}

	private long isHavingValidAccessTokenAndActive(String user, String apikey,
			String userType) throws ClassNotFoundException, SQLException{
		String query = "";
		sqlConnection = JDBCFactory.getMkAuthJDBCInstance();
		Statement getUserDetails = sqlConnection.createStatement();
		if(userType.equals("admin")){
			query = "SELECT id,a_user_name FROM mk_api_user_admin WHERE a_user_name = \""+user+"\" AND a_access_token = \""+apikey+"\" AND a_is_active = 1";
		}else{
			query = "SELECT id,a_user_name FROM mk_api_consumer WHERE a_user_name = \""+user+"\" AND a_access_token = \""+apikey+"\" AND a_is_active = 1" ;
		}
        ResultSet userDetailsResultSet = getUserDetails.executeQuery(query);
        while(userDetailsResultSet.next()){
        	if(userDetailsResultSet.getString("a_user_name").equals(user)){
        		return (long)userDetailsResultSet.getInt("id");
        	}
        }
        sqlConnection.close();
        return 0;
	}

	private String getUserType(String user) throws ClassNotFoundException, SQLException {		
		sqlConnection = JDBCFactory.getMkAuthJDBCInstance();
		Statement getUserStatement = sqlConnection.createStatement();
		String query1 = "SELECT a_user_name FROM mk_api_user_admin WHERE a_user_name = \""+user+"\"";
        ResultSet userDetailsResultSet1 = getUserStatement.executeQuery(query1);
        while(userDetailsResultSet1.next()){
        	if(userDetailsResultSet1.getString("a_user_name").equals(user)){
        		return "admin";
        	}
        }
        String query2 = "SELECT a_user_name FROM mk_api_consumer WHERE a_user_name = \""+user+"\"";
        ResultSet userDetailsResultSet2 = getUserStatement.executeQuery(query2);
        while(userDetailsResultSet2.next()){
        	if(userDetailsResultSet2.getString("a_user_name").equals(user)){
        		return "consumer";
        	}
        }
        sqlConnection.close();
		return null;
	}
}
