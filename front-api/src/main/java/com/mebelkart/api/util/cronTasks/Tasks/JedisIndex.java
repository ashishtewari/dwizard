/**
 * 
 */
package com.mebelkart.api.util.cronTasks.Tasks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.util.cronTasks.dao.JedisDao;
import com.mebelkart.api.util.factories.JedisFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.OnApplicationStart;

/**
 * @author Tinku
 *
 */
@OnApplicationStart
public class JedisIndex extends Job {

	JedisPool pool = new JedisFactory().getJedisConnectionPool();

	public void doJob() {
		System.out.println("Outside Job");
		try {
			System.out.println("In try");
			JedisDao jedisDaoObject = new JedisDao();
			System.out.println("In Do Job --------- Jedis caching started");			
			System.out.println("In Do Job --------- caching customer credentials");
			/**
			 * getting new consumer details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
			ResultSet unIndexedConsumerDetailsResultSet = jedisDaoObject.getConsumerDetails();
			while (unIndexedConsumerDetailsResultSet.next()) {
				System.out.println("In first while");
				int consumerId = unIndexedConsumerDetailsResultSet.getInt("id");
				System.out.println("Consumer mail is "+unIndexedConsumerDetailsResultSet.getString("a_user_name"));
				System.out.println("Consumer id is "+consumerId);
				// It returns the resource permissions result set of a single
				// user based on consumer Id
				ResultSet resourcePermissionsResultSet = jedisDaoObject.getResourcePermissionsDetails(consumerId);
				String getFunctionNames = "";
				HashMap<String, String> resourcePermissions = new HashMap<String, String>();
				while (resourcePermissionsResultSet.next()) {
					System.out.println("In second while");
					// It returns the unique resource id of specific user based on consumer Id
					int resourceId = resourcePermissionsResultSet.getInt("a_resource_id");
					// gets the resource name based on the resource id
					ResultSet resourceNameResultSet = jedisDaoObject.getResourceName(resourceId);
					String resourceName = "";
					while(resourceNameResultSet.next()){
						resourceName = resourceNameResultSet.getString("a_resource_name");
					}
					System.out.println("resource name is "+resourceName);
					int haveGetPermission = resourcePermissionsResultSet.getInt("a_have_get_permission");
					// if this resource having get permission is equal to 1 then we get all get function names
					if (haveGetPermission == 1) {
						resourcePermissions.put(resourceName, "get");
						ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetails(consumerId,resourceId);
						while (functionPermssionsResultSet.next()) {
							getFunctionNames = getFunctionNames + functionPermssionsResultSet.getString("a_function_name") + ",";
						}
					}
				}
				Jedis jedis = pool.getResource();
				try {
					System.out.println("In jedis ");
					// save to redis
					String encryptedAccessToken = MD5Encoding
							.encrypt(unIndexedConsumerDetailsResultSet.getString("a_access_token"));
					Set<String> resourceKeys = resourcePermissions.keySet();
					if (getFunctionNames.length() > 0) {
						System.out.println("In Jedis setting hashes");
						jedis.hset(encryptedAccessToken, "userName",
								unIndexedConsumerDetailsResultSet.getString("a_user_name"));
						for (String key : resourceKeys) {
							jedis.hset(encryptedAccessToken, key,
									resourcePermissions.get(key));
						}
						jedis.hset(encryptedAccessToken, "getFunctions",
								getFunctionNames.substring(0, getFunctionNames.length()-1));
						jedis.hset(encryptedAccessToken, "maxCount",
								unIndexedConsumerDetailsResultSet.getInt("a_count_assigned")+"");
						jedis.hset(encryptedAccessToken, "isActive", 
								unIndexedConsumerDetailsResultSet.getInt("a_is_active")+"");
						jedisDaoObject.updateConsumerRedisIndexedStatus(consumerId);
						System.out.println("updated consumer data base");
					}
					pool.destroy();
				} catch (JedisException e) {
					// if something wrong happen, return it back to the pool
					if (null != jedis) {
						pool.returnBrokenResource(jedis);
						jedis = null;
					}
				} finally {
					// /it's important to return the Jedis instance to the pool
					// once you've finished using it
					if (null != jedis)
						pool.returnResource(jedis);
				}
			}
			
			
			System.out.println("In Do Job --------- caching Admin credentials");

			ResultSet unIndexedAdminDetailsResultSet = jedisDaoObject.getAdminDetails();
			while (unIndexedAdminDetailsResultSet.next()) {
				System.out.println("In first while");
				int adminId = unIndexedAdminDetailsResultSet.getInt("id");
				System.out.println("Admin mail is "+unIndexedAdminDetailsResultSet.getString("a_user_name"));
				System.out.println("Admin id is "+adminId);
				// It returns the resource permissions result set of a single
				// user based on admin Id
				ResultSet resourcePermissionsResultSet = jedisDaoObject.getResourcePermissionsDetailsOfAdmin(adminId);
				String getFunctions = "";
				String postFunctions = "";
				String putFunctions = "";
				HashMap<String, String> resourcePermissions = new HashMap<String, String>();
				while (resourcePermissionsResultSet.next()) {
					System.out.println("In second while");
					// It returns the unique resource id of specific user based on admin Id
					int resourceId = resourcePermissionsResultSet.getInt("a_resource_id");
					// gets the resource name based on the resource id
					ResultSet resourceNameResultSet = jedisDaoObject.getResourceName(resourceId);
					String resourceName = "";
					while(resourceNameResultSet.next()){
						resourceName = resourceNameResultSet.getString("a_resource_name");
					}
					System.out.println("resource name is "+resourceName);
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
						ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetailsOfAdmin(adminId,resourceId,"get");
						while (functionPermssionsResultSet.next()) {
							getFunctions = getFunctions + functionPermssionsResultSet.getString("a_function_name")+ ",";
						}
					}
					// if this resource having get permission is equal to 1 then we get all get function names
					if (havePostPermission == 1) {
						if(resourcePermissions.containsKey(resourceName)){
							String methodName = resourcePermissions.get(resourceName);
							resourcePermissions.put(resourceName, methodName+",post");
						}else
							resourcePermissions.put(resourceName, "post");
						ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetailsOfAdmin(adminId,resourceId,"post");
						while (functionPermssionsResultSet.next()) {
							postFunctions = postFunctions + functionPermssionsResultSet.getString("a_function_name")+ ",";
						}
					}
					// if this resource having get permission is equal to 1 then we get all get function names
					if (havePutPermission == 1) {
						if(resourcePermissions.containsKey(resourceName)){
							String methodName = resourcePermissions.get(resourceName);
							resourcePermissions.put(resourceName, methodName+",put");
						}else
							resourcePermissions.put(resourceName, "put");
						ResultSet functionPermssionsResultSet = jedisDaoObject.getFunctionPermissionsDetailsOfAdmin(adminId,resourceId,"put");
						while (functionPermssionsResultSet.next()) {
							putFunctions = putFunctions + functionPermssionsResultSet.getString("a_function_name")+ ",";
						}
					}
				}
				Jedis jedis = pool.getResource();
				try {
					System.out.println("In jedis ");
					// save to redis
					String encryptedAccessToken = MD5Encoding
							.encrypt(unIndexedAdminDetailsResultSet.getString("a_access_token"));
					Set<String> resourceKeys = resourcePermissions.keySet();
					if (getFunctions.length() > 0 || postFunctions.length() > 0 || putFunctions.length() > 0) {
						System.out.println("In Jedis setting hashes");
						jedis.hset(encryptedAccessToken, "userName",
								unIndexedAdminDetailsResultSet.getString("a_user_name"));
						for (String key : resourceKeys) {
							jedis.hset(encryptedAccessToken, key,
									resourcePermissions.get(key));
						}
						System.out.println("getFunctions are "+getFunctions);
						jedis.hset(encryptedAccessToken, "getFunctions",
								getFunctions.substring(0, getFunctions.length()-1));
						
						System.out.println("postFunctions are "+postFunctions);
						jedis.hset(encryptedAccessToken, "postFunctions",
								postFunctions.substring(0, postFunctions.length()-1));
						
						System.out.println("putFunctions are "+putFunctions);
						jedis.hset(encryptedAccessToken, "putFunctions",
								putFunctions.substring(0, putFunctions.length()-1));
						// we are setting 1 lakh hits per hour for admin
						jedis.hset(encryptedAccessToken, "maxCount","100000");
						jedis.hset(encryptedAccessToken, "isActive",
								unIndexedAdminDetailsResultSet.getInt("a_is_active")+"");
						jedisDaoObject.updateAdminRedisIndexedStatus(adminId);
						System.out.println("updated admin data base");
					}
					pool.destroy();
				} catch (JedisException e) {
					// if something wrong happen, return it back to the pool
					if (null != jedis) {
						pool.returnBrokenResource(jedis);
						jedis = null;
					}
				} finally {
					// /it's important to return the Jedis instance to the pool
					// once you've finished using it
					if (null != jedis)
						pool.returnResource(jedis);
				}
			}
			System.out.println("In Do Job --------- Jedis caching stopped");
		} catch (SQLException e) {
			System.out.println("----------SQLException in JedisIndex----------");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("----------NullPointerException in JedisIndex---------");
		}
	}
}
