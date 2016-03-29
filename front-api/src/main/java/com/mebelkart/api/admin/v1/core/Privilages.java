/**
 * 
 */
package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class Privilages {

	private String resourceName;
	private long GET;
	private long POST;
	private long PUT;
	private long DELETE;
	/**
	 * Default Constructer
	 */
	public Privilages() {
		// TODO Auto-generated constructor stub
		this.setResourceName(null);
		this.setGET(0);
		this.setPOST(0);
		this.setPUT(0);
		this.setDELETE(0);
	}
	/**
	 * Parameterised Constructer
	 * @param resourceName
	 * @param get
	 * @param post
	 * @param put
	 * @param delete
	 */
	public Privilages(String resourceName,long get,long post,long put,long delete) {
		// TODO Auto-generated constructor stub
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
		return GET;
	}
	/**
	 * This is a setter
	 * @param gET
	 */
	public void setGET(long gET) {
		GET = gET;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getPUT() {
		return PUT;
	}
	/**
	 * This is a setter
	 * @param pUT
	 */
	public void setPUT(long pUT) {
		PUT = pUT;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getPOST() {
		return POST;
	}
	/**
	 * This is a setter
	 * @param pOST
	 */
	public void setPOST(long pOST) {
		POST = pOST;
	}
	/**
	 * This is a getter
	 * @return
	 */
	public long getDELETE() {
		return DELETE;
	}
	/**
	 * This is a setter
	 * @param dELETE
	 */
	public void setDELETE(long dELETE) {
		DELETE = dELETE;
	}

}
