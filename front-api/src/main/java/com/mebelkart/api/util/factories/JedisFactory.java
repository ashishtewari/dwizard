/**
 * 
 */
package com.mebelkart.api.util.factories;

import com.mebelkart.api.mkApiConfiguration;
import com.mebelkart.api.admin.v1.crypting.MD5Encoding;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

/**
 * @author Tinku
 *
 */
public class JedisFactory {

	// the jedis connection pool..
	private static JedisPool pool = null;

	public JedisFactory() {
		// configure our pool connection
		pool = new JedisPool(mkApiConfiguration.getRedisHost(), mkApiConfiguration.getRedisPort());
	}
	
	public JedisPool getJedisConnectionPool(){
		// get a jedis connection pool
		return pool;
	}

	@SuppressWarnings("static-access")
	public void validate(String user, String apikey, String resourceName, String method, String functionName) throws Exception {
		// encrypting the apikey to match with the apikey in the redis, which is
		// MD5 encrypted
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
								if (isBelowRateLimit(user)) {
									// Api Permission Granted
									incrementCurrentCount(user);
									//return 1;
								} else {
									// User's Rate Limit has Exceeded
									//return -6;
									throw new Exception("Your Rate Limit Exceeded");
								}
							}else{
								// User doesn't have access to this function
								//return -5;
								throw new Exception("You don't have access to this function");
							}
						} else {
							// User doesn't have Access to this Method
							//return -4;
							throw new Exception("You don't have Access to this Method");
						}
					} else {
						// User doesn't have Access to this Resource
						//return -3;
						throw new Exception("You don't have Access to this Resource");
					}
				} else {
					// User is Not in Active State 
					//return -2;
					throw new Exception("you are not in Active State");
				}
			}else{
				// Not a Valid Access Token
				//return -1;
				throw new Exception("You don't have Valid Access Token");
			}
		} else {
			// Not a Valid User
			//return 0;
			throw new Exception("you are not a Valid User");
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
				return jedis.hget(user, "getFunctions").contains(functionName);
			else if(method.equals("post"))
				return jedis.hget(user, "postFunctions").contains(functionName);
			else if(method.equals("put"))
				return jedis.hget(user, "putFunctions").contains(functionName);
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

	public boolean isBelowRateLimit(String user) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			if((currentCount(user) < maxCount(user)) && currentCount(user) > -1 && maxCount(user) > -1){
				return true;
			}else{
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
	
	public void incrementCurrentCount(String userName) {
		// get a jedis connection jedis connection pool
		Jedis jedis = pool.getResource();
		try {
			int currentCount = currentCount(userName);
			currentCount++;
			jedis.hset(userName, "currentCount" ,currentCount+"");
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
			return Integer.parseInt(jedis.hget(userName,"currentCount"));
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
