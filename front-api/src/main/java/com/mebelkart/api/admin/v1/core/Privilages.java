/**
 * 
 */
package com.mebelkart.api.admin.v1.core;

import java.util.List;

/**
 * @author Tinku
 *
 */
public class Privilages {

	private long resourceId;
	private String resourceName;
	private long get;
	private long post;
	private long put;
	private long delete;
	private List<String> getFunctions;
	private List<String> postFunctions;
	private List<String> putFunctions;
	/**
	 * Default Constructer
	 */
	public Privilages() {
		this.setResourceId(0);
		this.setResourceName(null);
		this.setGET(0);
		this.setPOST(0);
		this.setPUT(0);
		this.setDELETE(0);
		this.setGetFunctions(null);
		this.setPostFunctions(null);
		this.setPutFunctions(null);
	}
	/**
	 * Parameterised Constructer
	 * @param resourceName
	 * @param get
	 * @param post
	 * @param put
	 * @param delete
	 */
	public Privilages(long resourceId,String resourceName,long get,long post,long put,long delete) {
		this.setResourceId(resourceId);
		this.setResourceName(resourceName);
		this.setGET(get);
		this.setPOST(post);
		this.setPUT(put);
		this.setDELETE(delete);
	}
	/**
	 * This is a getter
	 * @return
	 */
	public String getResourceName() {
		return resourceName;
	}
	/**
	 * This is a setter
	 * @param resourceName
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getGET() {
		return get;
	}
	/**
	 * This is a setter
	 * @param gET
	 */
	public void setGET(long gET) {
		get = gET;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getPUT() {
		return put;
	}
	/**
	 * This is a setter
	 * @param pUT
	 */
	public void setPUT(long pUT) {
		put = pUT;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getPOST() {
		return post;
	}
	/**
	 * This is a setter
	 * @param pOST
	 */
	public void setPOST(long pOST) {
		post = pOST;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getDELETE() {
		return delete;
	}
	/**
	 * This is a setter
	 * @param dELETE
	 */
	public void setDELETE(long dELETE) {
		delete = dELETE;
	}
	/**
	 * This returns all getFunctions
	 * @return
	 */
	public List<String> getGetFunctions() {
		return getFunctions;
	}
	/**
	 * This will set all getFunctions 
	 * @param getFunctions
	 */
	public void setGetFunctions(List<String> getFunctions) {
		this.getFunctions = getFunctions;
	}
	/**
	 * This will return all postFunctions
	 * @return
	 */
	public List<String> getPostFunctions() {
		return postFunctions;
	}
	/**
	 * This will set postFunctions
	 * @param postFunctions
	 */
	public void setPostFunctions(List<String> postFunctions) {
		this.postFunctions = postFunctions;
	}
	/**
	 * This will return all putFunctions
	 * @return
	 */
	public List<String> getPutFunctions() {
		return putFunctions;
	}
	/**
	 * This will set putFunctions
	 * @param putFunctions
	 */
	public void setPutFunctions(List<String> putFunctions) {
		this.putFunctions = putFunctions;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

}
