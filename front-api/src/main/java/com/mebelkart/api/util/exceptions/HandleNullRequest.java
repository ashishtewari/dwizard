/**
 * 
 */
package com.mebelkart.api.util.exceptions;


import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

	
/**
 * @author Tinku
 *
 */
public class HandleNullRequest implements ExceptionMapper<WebApplicationException>  {
	
	@SuppressWarnings("serial")
	/* (non-Javadoc)
	 * @see javax.ws.rs.ext.ExceptionMapper#toResponse(java.lang.Throwable)
	 */
	@Override
	public Response toResponse(WebApplicationException e) {
		int status = e.getResponse() == null ? 404 : e.getResponse().getStatus();
		final String statusCode = e.getResponse().getStatus()+"";
		final String message = e.getMessage();
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new HashMap<String, String>() { {             	
                    put("status", statusCode); 
                    put("message",message ); 
                } }).build();
	}

}
