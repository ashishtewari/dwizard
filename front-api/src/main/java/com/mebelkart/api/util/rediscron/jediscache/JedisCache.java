/**
 * 
 */
package com.mebelkart.api.util.rediscron.jediscache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisDataException;

import com.mebelkart.api.mkApiConfiguration;
import com.mebelkart.api.util.factories.JedisFactory;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.On;

/**
 * @author Tinku
 *
 */
/** Fire at 12.01am (early morning) every day **/
@On("0 01 0 * * ?")
public class JedisCache extends Job {
	/**
	 * Get actual class name to be printed on
	 */
	static Logger log = LoggerFactory.getLogger(JedisCache.class);
	
	JedisFactory jedisFactory = new JedisFactory();
	Jedis jedis = jedisFactory.getPool().getResource();
	/**
	 * This job performs a redis key delete operation every day at 12.01am. 
	 * So that it gets cached into redis when ever a first user of that day access deals page
	 * It checks for redis key "dealsPage" which doesn't exist. So now it executes query and get cached into redis 
	 */
	public void doJob() {
		try {
			jedis.auth(mkApiConfiguration.getRedisPassword());			
			if(jedis.exists("dealsPage")){
				jedis.del("dealsPage");
			}
			if(jedis.exists("dealsOfTheDay")){
				jedis.del("dealsOfTheDay");
			}
		}catch(JedisDataException e){
			e.printStackTrace();
		}
	}
}
