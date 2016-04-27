/**
 * 
 */
package com.mebelkart.api.util.factories;

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
	
	public JedisPool getJedisConnectionPool(){
		// get a jedis connection pool
		return pool;
	}

	@SuppressWarnings("static-access")
	public int validate(String user, String apikey, String resourceName, String method, String functionName) {
		// encrypting the apikey to match with the apikey in the redis, which is
		// MD5 encrypted
		String userName = user;
		MD5Encoding encode = new MD5Encoding();
		apikey = encode.encrypt(apikey);
		user = encode.encrypt(user);
		// checks if the userName key exists in redis database or not
		if (isValidUser(user)) {
			// checks if this particular user is having valid apikey or not
			if(isValidAccessToken(user,apikey)){
				// checks whether the user is in active state or not
				if (isActive(user)) {
					// checks if he had access to that particular resource
					if (containsResource(user, resourceName)) {
						// checks if he had access to that particular method
						if (containsMethod(user, resourceName, method)) {
							// checks if he had access to this particular function
							if(containsFunction(user,method,functionName)){
								// checks for ratelimit
								if (isBelowRateLimit(user, userName)) {
									// Api Permission Granted
									incrementCurrentCount(userName);
									return 1;
								} else {
									// User's Rate Limit has Exceeded
									return -6;
								}
							}else{
								// User doesn't have access to this function
								return -5;
							}
						} else {
							// User doesn't have Access to this Method
							return -4;
						}
					} else {
						// User doesn't have Access to this Resource
						return -3;
					}
				} else {
					// User is Not in Active State 
					return -2;
				}
			}else{
				// Not a Valid Access Token
				return -1;
			}
		} else {
			// Not a Valid User
			return 0;
		}
	}

	private boolean isValidUser(String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.exists(userName);
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

	private boolean containsFunction(String user, String method,String functionName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try{
			if(method.equals("get"))
				return jedis.hget(user, "getFuncions").contains(functionName);
			else if(method.equals("post"))
				return jedis.hget(user, "postFuncions").contains(functionName);
			else if(method.equals("put"))
				return jedis.hget(user, "putFuncions").contains(functionName);
		}catch (JedisException e) {
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
		return false;
	}

	public boolean isValidAccessToken(String userName,String accessToken) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if(jedis.hget(userName, "accessToken").equals(accessToken))
				return true;
			else
				return false;
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

	public boolean isActive(String user) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if (jedis.hget(user, "isActive").equals("0")) {
				return false;
			} else if (jedis.hget(user, "isActive").equals("1")) {
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

//	public String getUserName(String accessToken) {
//		// get a jedis connection jedis connection pool
//		Jedis jedis = pool.getResource();
//		try {
//			return jedis.hget(accessToken, "userName");
//		} catch (JedisException e) {
//			// if something wrong happen, return it back to the pool
//			if (null != jedis) {
//				pool.returnBrokenResource(jedis);
//				jedis = null;
//			}
//			return null;
//		} finally {
//			// /it's important to return the Jedis instance to the pool once
//			// you've finished using it
//			if (null != jedis)
//				pool.returnResource(jedis);
//		}
//	}

	public boolean isBelowRateLimit(String user, String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if (jedis.exists(userName+":accessCount")) {
				if((currentCount(userName+":accessCount") < maxCount(user)) && currentCount(userName+":accessCount") > -1 && maxCount(user) > -1){
					return true;
				}else{
					return false;
				}
			} else {
				// setting temporary ratelimit counter based on userName for 1 day 10 min
				// i.e.,87000 seconds and it expires after every 87000 seconds or when ever the redis is re-indexed after 24 hours i.e.,86400 seconds 
				jedis.setex(userName+":accessCount", 87000, "0");
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

	public boolean containsResource(String user, String resourceName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.hexists(user, resourceName);
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

	public boolean containsMethod(String user, String resourceName, String method) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			return jedis.hget(user, resourceName).contains(method);
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
