/**
 * 
 */
package com.mebelkart.api.util.rediscron.jedis;

import com.mebelkart.api.util.rediscron.dao.JedisDao;
import com.mebelkart.api.util.crypting.MD5Encoding;
import com.mebelkart.api.util.factories.JedisFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.exceptions.JedisException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tinku
 *
 */

/** Fire at 3.00am (early morning) every day **/
@OnApplicationStart
@On("0 0 3 * * ?")
public class JedisIndex extends Job {
	/**
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(JedisIndex.class);
	
	JedisFactory jedisFactory = new JedisFactory();

	public void doJob() {
		try {
			JedisDao jedisDaoObject = new JedisDao();
			//jedis.auth(mkApiConfiguration.getRedisPassword());
			/**
			 * getting new consumer details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
			ResultSet unIndexedConsumerDetailsResultSet = jedisDaoObject.getConsumerDetails();
			redisIndex("consumer",jedisDaoObject,unIndexedConsumerDetailsResultSet);			
			
			/**
			 * getting new admin details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
			ResultSet unIndexedAdminDetailsResultSet = jedisDaoObject.getAdminDetails();
			redisIndex("admin",jedisDaoObject,unIndexedAdminDetailsResultSet);
		} catch (SQLException e) {
			System.out.println("----------SQLException in JedisIndex----------");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("----------NullPointerException in JedisIndex---------");
			e.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (JedisDataException e){
			e.printStackTrace();
			log.info("There is an JedisDataException in JedisIndex, Message is "+e.getMessage());
		} 
	}
	
	private void redisIndex(String userType,JedisDao jedisDaoObject,ResultSet unIndexedUserDetailsResultSet) 
			throws JedisDataException, SQLException{
		String resourcePermissionTableName;
		String functionPermissionTableName;
		String functionPermissionTableColName;
		String userTable;
		if(userType.equals("consumer")){
			userTable = "mk_api_consumer";
			resourcePermissionTableName = "mk_api_resources_consumer_permission";
			functionPermissionTableName = "mk_api_resources_consumer_function_permission";
			functionPermissionTableColName = "a_consumer_id";
		}else{
			userTable = "mk_api_user_admin";
			resourcePermissionTableName = "mk_api_resources_admin_permission";
			functionPermissionTableName = "mk_api_resources_admin_function_permission";
			functionPermissionTableColName = "a_admin_id";
		}
		while (unIndexedUserDetailsResultSet.next()) {
			int userId = unIndexedUserDetailsResultSet.getInt("id");
			String nameOfUser = unIndexedUserDetailsResultSet.getString("a_user_name");
			System.out.println("userType is "+userType+" and userName is "+nameOfUser);
			// It returns the resource permissions result set of a single
			// user based on consumer Id
			ResultSet resourcePermissionsResultSet = jedisDaoObject.getResourcePermissionsDetails(userId,resourcePermissionTableName,functionPermissionTableColName);
			String getFunctions = "";
			String postFunctions = "";
			String putFunctions = "";
			HashMap<String, String> resourcePermissions = new HashMap<String, String>();
			while (resourcePermissionsResultSet.next()) {
				// It returns the unique resource id of specific user based on consumer Id
				int resourceId = resourcePermissionsResultSet.getInt("a_resource_id");
				// gets the resource name based on the resource id
				ResultSet resourceNameResultSet = jedisDaoObject.getResourceName(resourceId);
				String resourceName = "";
				while(resourceNameResultSet.next()){
					resourceName = resourceNameResultSet.getString("a_resource_name");
				}
				int haveGetPermission = resourcePermissionsResultSet.getInt("a_have_get_permission");
				int havePostPermission = resourcePermissionsResultSet.getInt("a_have_post_permission");
				int havePutPermission = resourcePermissionsResultSet.getInt("a_have_put_permission");
				// if this resource having get permission is equal to 1 then we get all get function names
				if (haveGetPermission == 1) {
					if(resourcePermissions.containsKey(resourceName)){
						String methodName = resourcePermissions.get(resourceName);
						resourcePermissions.put(resourceName, methodName+",get");
					}else
						resourcePermissions.put(resourceName, "get");
					ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetails(userId,resourceId,"get",functionPermissionTableName,functionPermissionTableColName);
					while (functionPermssionsResultSet.next()) {
						getFunctions = getFunctions + functionPermssionsResultSet.getString("a_function_name") + ",";
					}
				}
				// if this resource having post permission is equal to 1 then we get all get function names
				if (havePostPermission == 1) {
					if(resourcePermissions.containsKey(resourceName)){
						String methodName = resourcePermissions.get(resourceName);
						resourcePermissions.put(resourceName, methodName+",post");
					}else
						resourcePermissions.put(resourceName, "post");
					ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetails(userId,resourceId,"post",functionPermissionTableName,functionPermissionTableColName);
					while (functionPermssionsResultSet.next()) {
						postFunctions = postFunctions + functionPermssionsResultSet.getString("a_function_name")+ ",";
					}
					functionPermssionsResultSet.close();
				}
				// if this resource having put permission is equal to 1 then we get all get function names
				if (havePutPermission == 1) {
					if(resourcePermissions.containsKey(resourceName)){
						String methodName = resourcePermissions.get(resourceName);
						resourcePermissions.put(resourceName, methodName+",put");
					}else
						resourcePermissions.put(resourceName, "put");
					ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetails(userId,resourceId,"put",functionPermissionTableName,functionPermissionTableColName);
					while (functionPermssionsResultSet.next()) {
						putFunctions = putFunctions + functionPermssionsResultSet.getString("a_function_name")+ ",";
					}
					functionPermssionsResultSet.close();
				}
			}
			Jedis jedis = jedisFactory.getPool().getResource();
			try {
				if(jedis.exists(MD5Encoding.encrypt(nameOfUser))){
					// removing that key and its all related fields and values form redis
					jedis.del(MD5Encoding.encrypt(nameOfUser));
					System.out.println(nameOfUser+" is already in Redis and deleted now");
				}
				// save to redis
				String userName = nameOfUser; // Here it is not encoded 
				String encryptedAccessToken = MD5Encoding
						.encrypt(unIndexedUserDetailsResultSet.getString("a_access_token"));
				nameOfUser = MD5Encoding.encrypt(nameOfUser); // Here it is encoded
				Set<String> resourceKeys = resourcePermissions.keySet();
				if (getFunctions.length() > 0 || postFunctions.length() > 0 || putFunctions.length() > 0) {
					jedis.hset(nameOfUser, "userName", userName);
					jedis.hset(nameOfUser, "accessToken",encryptedAccessToken);
					for (String key : resourceKeys) {
						jedis.hset(nameOfUser, key,resourcePermissions.get(key));
					}
					if(getFunctions.length() > 0)
						jedis.hset(nameOfUser, "getFunctions",
								getFunctions.substring(0, getFunctions.length()-1));
					if(postFunctions.length() > 0)
						jedis.hset(nameOfUser, "postFunctions",
								postFunctions.substring(0, postFunctions.length()-1));
					if(putFunctions.length() > 0)
						jedis.hset(nameOfUser, "putFunctions",
									putFunctions.substring(0, putFunctions.length()-1));
					if(userType.equals("consumer")){
						jedis.hset(nameOfUser, "maxCount",unIndexedUserDetailsResultSet.getInt("a_count_assigned")+"");
						jedis.hset(nameOfUser, "adminLevel", "undefined");
					}else{
						jedis.hset(nameOfUser, "maxCount","100000");
						jedis.hset(nameOfUser, "adminLevel", unIndexedUserDetailsResultSet.getInt("a_admin_level")+"");
					}
					jedis.hset(nameOfUser, "currentCount","0");
					jedis.hset(nameOfUser, "isActive",
							unIndexedUserDetailsResultSet.getInt("a_is_active")+"");
					jedisDaoObject.updateConsumerRedisIndexedStatus(userId,userTable);
					System.out.println(userName+" is cached into redis");
				}
			} catch (JedisException e) {
				// if something wrong happen, return it back to the pool
				if (null != jedis) {
					jedisFactory.getPool().returnBrokenResource(jedis);
					jedis = null;
				}
			} finally {
				// it's important to return the Jedis instance to the pool once
				// you've finished using it
				if (null != jedis)
					jedisFactory.getPool().returnResource(jedis);
			}
		}
	}
}
