/**
 * 
 */
package com.mebelkart.api.util.cronTasks.jedis;

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
			 * getting all consumer names from db and checking whether they are redis indexed
			 * If it is so then we are getting the currentCount of the user and
			 * updating it in mk_api_statics table and 
			 * then later updating the currentCount of the user to 0 in redis
			 */			
			ResultSet allConsumerNames = jedisDaoObject.getAllConsumerNames();
			Jedis tempJedis = pool.getResource();
			while(allConsumerNames.next()){
				int customerId = allConsumerNames.getInt("id");
				String customerName = allConsumerNames.getString("a_user_name");
				if(tempJedis.exists(MD5Encoding.encrypt(customerName))){
					// getting previous day count
					int dayCount = Integer.parseInt(tempJedis.hget(customerName,"currentCount"));
					// getting previous days date and time
					String previousDate = new HelperMethods().getYesterdayDateString();
					// updating this details in mk_api_statics table
					jedisDaoObject.updateHitsAndDate(customerId,dayCount,previousDate);
					// updating currentCount of this user to 0
					tempJedis.hset(MD5Encoding.encrypt(customerName), "currentCount","0");
				}
			}
			/**
			 * getting new consumer details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
			ResultSet unIndexedConsumerDetailsResultSet = jedisDaoObject.getConsumerDetails();
			List<String> indexedUserNames = new ArrayList<String>();
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
						// removing that key and its all related fields and values form redis
						jedis.del(MD5Encoding.encrypt(customerName));
					}
					// save to redis
					String userName = customerName; // Here it is not encoded 
					String encryptedAccessToken = MD5Encoding
							.encrypt(unIndexedConsumerDetailsResultSet.getString("a_access_token"));
					customerName = MD5Encoding.encrypt(customerName); // Here it is encoded
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
						jedis.hset(customerName, "currentCount","0");
						jedis.hset(customerName, "isActive",
								unIndexedConsumerDetailsResultSet.getInt("a_is_active")+"");
//						// setting temporary ratelimit counter based on userName for 1 day 10 min
//						// i.e.,87000 seconds and it expires after every 87000 seconds or when ever the redis is re-indexed after 24 hours i.e.,86400 seconds
//						jedis.setex(userName+":accessCount", 87000, "0");
						jedisDaoObject.updateConsumerRedisIndexedStatus(consumerId);
						indexedUserNames.add(userName);
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
			
			/**
			 * getting new admin details from mk_api which are not
			 * redisIndexed or may be changed after last indexing
			 */
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
						jedis.hset(adminName, "currentCount","0");
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
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
