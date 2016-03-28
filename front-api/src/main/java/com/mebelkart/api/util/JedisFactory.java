/**
 * 
 */
package com.mebelkart.api.util;

import com.mebelkart.api.admin.v1.crypting.MD5Encoding;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author Tinku
 *
 */
public class JedisFactory {

	// address of your redis server
	private static final String redisHost = "localhost";
	private static final Integer redisPort = 6379;

	// the jedis connection pool..
	private static JedisPool pool = null;

	public JedisFactory() {
		// configure our pool connection
		pool = new JedisPool(redisHost, redisPort);

	}

	@SuppressWarnings("static-access")
	public int validate(String apikey, String resourceName, String method) {
		// encrypting the apikey to match with the apikey in the redis, which is
		// MD5 encrypted
		MD5Encoding encode = new MD5Encoding();
		apikey = encode.encrypt(apikey);
		// checks if the apikey exists in redis database or not
		if (isValidAccessToken(apikey)) {
			String userName = getUserName(apikey);
			// checks whether the user is in active state or not
			if (isActive(apikey)) {
				// checks if he had access to that particular resource
				if (containsResource(apikey, resourceName)) {
					// checks if he had access to that particular method
					if (containsMethod(apikey, resourceName, method)) {
						// checks for ratelimit
						if (isBelowRateLimit(apikey, userName)) {
							// Api Permission Granted
							incrementCurrentCount(userName);
							return 1;
						} else {
							// User's Rate Limit has Exceeded
							return -4;
						}
					} else {
						// User doesn't have Access to this Method
						return -3;
					}
				} else {
					// User doesn't have Access to this Resource
					return -2;
				}
			} else {
				// User is Not in Active State 
				return -1;
			}
		} else {
			// Not a Valid Access Token
			return 0;
		}
	}

	public boolean isValidAccessToken(String accessToken) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.exists(accessToken);
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			// it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}

	public boolean isActive(String accessToken) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if (jedis.hget(accessToken, "isActive").equals("0")) {
				return false;
			} else if (jedis.hget(accessToken, "isActive").equals("1")) {
				return true;
			} else {
				return false;
			}
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}

	public String getUserName(String accessToken) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(accessToken, "userName");
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return null;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}

	public boolean isBelowRateLimit(String accessToken, String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if (jedis.exists(userName+":accessCount")) {
				if((currentCount(userName+":accessCount") < maxCount(accessToken)) && currentCount(userName+":accessCount") > -1 && maxCount(accessToken) > -1){
					return true;
				}else{
					return false;
				}
			} else {
				jedis.setex(userName+":accessCount", 3600, "0");
				return true;
			}
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}
	
	public void incrementCurrentCount(String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			jedis.incrBy(userName+":accessCount", 1);
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}
	
	public int currentCount(String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return Integer.parseInt(jedis.get(userName));
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return -1;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}
	
	public int maxCount(String accessToken) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return Integer.parseInt(jedis.hget(accessToken, "maxCount"));
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return -1;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}

	public boolean containsResource(String accessToken, String resourceName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.hexists(accessToken, resourceName.toUpperCase());
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}

	public boolean containsMethod(String accessToken, String resourceName, String method) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(accessToken, resourceName).toUpperCase().contains(method.toUpperCase());
		} catch (JedisException e) {
			// if something wrong happen, return it back to the pool
			if (null != jedis) {
				pool.returnBrokenResource(jedis);
				jedis = null;
			}
			return false;
		} finally {
			// /it's important to return the Jedis instance to the pool once
			// you've finished using it
			if (null != jedis)
				pool.returnResource(jedis);
		}
	}
}
