package com.mebelkart.api.util.rediscron.jedis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import com.mebelkart.api.util.rediscron.dao.JedisDao;
import com.mebelkart.api.util.factories.JedisFactory;
import com.mebelkart.api.util.helpers.Helper;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;
/** Fire at 2.30am (early morning) every day **/

@On("0 30 2 * * ?")
public class JedisStatics extends Job{
	
	/**
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(JedisStatics.class);
	
	JedisFactory jedisFactory = new JedisFactory();
	
	public void doJob() {
		try{
			JedisDao jedisDaoObject = new JedisDao();
			/**
			 * getting all redis indexed keys and then we are getting the currentCount of the user and
			 * updating it in mk_api_statics table and 
			 * then later updating the currentCount of the user to 0 in redis
			 */	
			Jedis tempJedis = jedisFactory.getJedisConnection();
			Set<String> keys = tempJedis.keys("*");
			for(String key: keys){
				try{
					String customerName = tempJedis.hget(key, "userName");
					int customerId = 0;
					ResultSet consumerIds = jedisDaoObject.getConsumerId(customerName);
					while(consumerIds.next())
						customerId = consumerIds.getInt("id");
					if(customerId != 0){  
						System.out.println(customerName+" was indexed in redis and updating his statics");
						// getting previous day count
						int dayCount = Integer.parseInt(tempJedis.hget(key,"currentCount"));
						// getting previous days date and time
						String previousDate = new Helper().getYesterdayDateString();
						// updating this details in mk_api_statics table
						jedisDaoObject.updateHitsAndDate(customerId,dayCount,previousDate);
						// updating currentCount of this user to 0
						tempJedis.hset(key, "currentCount","0");
						// Check whether this user is deactivated in DB or not
						// If so then remove him from redis
						if(isUserDeactivated(customerId)){
							System.out.println(customerName + " was deactivated in DB, Removing his/her key in redis");
							tempJedis.del(key);
						}
					}else{
						// checking whether the user is admin and checking if he is deactivated in DB
						ResultSet adminIds = jedisDaoObject.getAdminId(customerName);
						int adminId = 0;
						while(adminIds.next()){
							adminId = adminIds.getInt("id");
						}
						if(adminId != 0){
							// Check whether this user is deactivated in DB or not
							// If so then remove him from redis
							if(isAdminDeactivated(adminId)){
								System.out.println(customerName+" was indexed in redis");
								System.out.println(customerName + " was deactivated in DB, Removing his/her key in redis");
								tempJedis.del(key);
							}
						}
					}
				} catch(JedisDataException e){
					System.out.println("------------ Jedis Exception, Inavlid Hash key --------------");
				}
			}
		}catch (SQLException e) {
			System.out.println("----------SQLException in JedisStatics----------");
			e.printStackTrace();
		} catch (NullPointerException e) {
			System.out.println("----------NullPointerException in JedisStatics---------");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (JedisDataException e){
			log.info("There is an JedisDataException in JedisStatics, Message is "+e.getMessage());
		}
	}

	private boolean isAdminDeactivated(int adminId) {
		try {
			JedisDao jedisDaoObject = new JedisDao();
			int activeStatus = -1;
			ResultSet adminDetails = jedisDaoObject.getAdminStatus(adminId);
			while(adminDetails.next()){
				if(adminDetails.getInt("a_is_active") == 0)
					activeStatus = 0;
				else 
					activeStatus = 1;
			}
			if(activeStatus == 0)
				return true;
			else 
				return false;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in jedis statics at isAdminDeactivated method");
			//e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQLException in jedis statics at isAdminDeactivated method");
			//e.printStackTrace();
		}
		return false;
	}

	private boolean isUserDeactivated(int customerId) {
		try {
			JedisDao jedisDaoObject = new JedisDao();
			int activeStatus = -1;
			ResultSet customerDetails = jedisDaoObject.getConsumerStatus(customerId);
			while(customerDetails.next()){
				if(customerDetails.getInt("a_is_active") == 0)
					activeStatus = 0;
				else 
					activeStatus = 1;
			}
			if(activeStatus == 0)
				return true;
			else 
				return false;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException in jedis statics at isUserDeactivated method");
			//e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("SQLException in jedis statics at isUserDeactivated method");
			//e.printStackTrace();
		}
		return false;
	}
}
