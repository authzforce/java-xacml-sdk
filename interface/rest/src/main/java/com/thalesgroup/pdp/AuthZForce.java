package com.thalesgroup.pdp;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/service")
/**
 * 
 * @author romain.ferrari[AT]thalesgroup.com
 * 
 * This interface represent the AuthZForce PDP
 */
public interface AuthZForce {

	// This method is called if TEXT_PLAIN is request
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	String sayPlainTextHello();

	/**
	 * 
	 * This method is called if GET method is used
	 * 
	 * @param userid
	 * @param resourceid
	 * @param actionid
	 * @return String
	 * 
	 */
	@Path("{user}/{resource}/{action}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	String pdpXmlGetEval(@PathParam("user") String userid,
			@PathParam("resource") String resourceid,
			@PathParam("action") String actionid);

	@POST
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.APPLICATION_XML)
	String pdpXmlPostXacmlEval(String request) throws IOException;

}