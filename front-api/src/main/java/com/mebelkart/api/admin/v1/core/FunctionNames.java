/**
 * 
 */
package com.mebelkart.api.admin.v1.core;

/**
 * @author Tinku
 *
 */
public class FunctionNames {
	private String functionName;
	private Integer functionId;

	/**
	 * Default Constructer
	 */
	public FunctionNames() {
	}
	
	public FunctionNames(Integer functionId,String functionName) {
		this.setFunctionName(functionName);
		this.setFunctionId(functionId);
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public Integer getFunctionId() {
		return functionId;
	}

	public void setFunctionId(Integer functionId) {
		this.functionId = functionId;
	}

}
