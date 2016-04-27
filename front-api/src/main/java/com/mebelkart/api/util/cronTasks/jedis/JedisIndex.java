/**
 * 
 */
package com.mebelkart.api.util.cronTasks.jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import com.mebelkart.api.admin.v1.crypting.MD5Encoding;
import com.mebelkart.api.admin.v1.helper.HelperMethods;
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

	JedisFactory jedisFactory = new JedisFactory();
	JedisPool pool = jedisFactory.getJedisConnectionPool();

	public void doJob() {
		try {
			JedisDao jedisDaoObject = new JedisDao();
			/**
			 * getting new consumer details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
			ResultSet unIndexedConsumerDetailsResultSet = jedisDaoObject.getConsumerDetails();
			while (unIndexedConsumerDetailsResultSet.next()) {
				int consumerId = unIndexedConsumerDetailsResultSet.getInt("id");
				String customerName = unIndexedConsumerDetailsResultSet.getString("a_user_name");
				// It returns the resource permissions result set of a single
				// user based on consumer Id
				ResultSet resourcePermissionsResultSet = jedisDaoObject.getResourcePermissionsDetails(consumerId);
				String getFunctionNames = "";
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
					if(jedis.exists(MD5Encoding.encrypt(customerName))){
						// getting previous day count
						int dayCount = 0;
						if(jedis.exists(customerName+":accessCount"))
							dayCount = Integer.parseInt(jedis.get(customerName+":accessCount"));
						else
							dayCount = 0;
						// getting previous days date and time
						String previousDate = new HelperMethods().getYesterdayDateString();
						// updating this details in mk_api_statics table
						jedisDaoObject.updateHitsAndDate(consumerId,dayCount,previousDate);
						// removing that key and its all related fields and values form redis
						jedis.del(MD5Encoding.encrypt(customerName));
					}
					// save to redis
					String encryptedAccessToken = MD5Encoding
							.encrypt(unIndexedConsumerDetailsResultSet.getString("a_access_token"));
					customerName = MD5Encoding.encrypt(customerName);
					Set<String> resourceKeys = resourcePermissions.keySet();
					if (getFunctionNames.length() > 0) {
						jedis.hset(customerName, "accessToken",encryptedAccessToken);
						for (String key : resourceKeys) {
							jedis.hset(customerName, key,resourcePermissions.get(key));
						}
						jedis.hset(customerName, "getFunctions",
								getFunctionNames.substring(0, getFunctionNames.length()-1));
						jedis.hset(customerName, "maxCount",
								unIndexedConsumerDetailsResultSet.getInt("a_count_assigned")+"");
						jedis.hset(customerName, "isActive", 
								unIndexedConsumerDetailsResultSet.getInt("a_is_active")+"");
						jedisDaoObject.updateConsumerRedisIndexedStatus(consumerId);
					}
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
			

			ResultSet unIndexedAdminDetailsResultSet = jedisDaoObject.getAdminDetails();
			while (unIndexedAdminDetailsResultSet.next()) {
				int adminId = unIndexedAdminDetailsResultSet.getInt("id");
				String adminName = unIndexedAdminDetailsResultSet.getString("a_user_name");
				// It returns the resource permissions result set of a single
				// user based on admin Id
				ResultSet resourcePermissionsResultSet = jedisDaoObject.getResourcePermissionsDetailsOfAdmin(adminId);
				String getFunctions = "";
				String postFunctions = "";
				String putFunctions = "";
				HashMap<String, String> resourcePermissions = new HashMap<String, String>();
				while (resourcePermissionsResultSet.next()) {
					// It returns the unique resource id of specific user based on admin Id
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
						functionPermssionsResultSet.close();
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
						functionPermssionsResultSet.close();
					}
				}
				Jedis jedis = pool.getResource();
				try {
					if(jedis.exists(MD5Encoding.encrypt(adminName))){
//						// getting previous day count
//						int dayCount = 0;
//						if(jedis.exists(adminName+":accessCount"))
//							dayCount = Integer.parseInt(jedis.get(adminName+":accessCount"));
//						else
//							dayCount = 0;
//						// getting previous days date and time
//						String previousDate = new HelperMethods().getYesterdayDateString();
//						// updating this details in mk_api_statics table
//						jedisDaoObject.updateHitsAndDate(adminId,dayCount,previousDate);
						// removing that key and its all related fields and values form redis
						jedis.del(MD5Encoding.encrypt(adminName));
					}
					// save to redis
					String encryptedAccessToken = MD5Encoding
							.encrypt(unIndexedAdminDetailsResultSet.getString("a_access_token"));
					adminName = MD5Encoding.encrypt(adminName);
					Set<String> resourceKeys = resourcePermissions.keySet();
					if (getFunctions.length() > 0 || postFunctions.length() > 0 || putFunctions.length() > 0) {
						jedis.hset(adminName, "accessToken",encryptedAccessToken);
						for (String key : resourceKeys) {
							jedis.hset(adminName, key,
									resourcePermissions.get(key));
						}
						jedis.hset(adminName, "getFunctions",
								getFunctions.substring(0, getFunctions.length()-1));
						
						jedis.hset(adminName, "postFunctions",
								postFunctions.substring(0, postFunctions.length()-1));
						
						jedis.hset(adminName, "putFunctions",
								putFunctions.substring(0, putFunctions.length()-1));
						// we are setting 1 lakh hits per hour for admin
						jedis.hset(adminName, "maxCount","100000");
						jedis.hset(adminName, "isActive",
								unIndexedAdminDetailsResultSet.getInt("a_is_active")+"");
						jedisDaoObject.updateAdminRedisIndexedStatus(adminId);
					}
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
		} catch (SQLException e) {
			System.out.println("----------SQLException in JedisIndex----------");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("----------NullPointerException in JedisIndex---------");
		}
	}
}
