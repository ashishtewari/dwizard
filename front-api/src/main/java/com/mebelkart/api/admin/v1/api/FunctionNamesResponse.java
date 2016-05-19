/**
 * 
 */
package com.mebelkart.api.admin.v1.api;

import java.util.List;

import com.mebelkart.api.admin.v1.core.FunctionNames;

/**
 * @author Tinku
 *
 */
public class FunctionNamesResponse {
	
	private List<FunctionNames> functions;

	/**
	 * 
	 */
	public FunctionNamesResponse(List<FunctionNames> functions) {
		this.setFunctions(functions);
	}

	public List<FunctionNames> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FunctionNames> functions) {
		this.functions = functions;
	}

}
