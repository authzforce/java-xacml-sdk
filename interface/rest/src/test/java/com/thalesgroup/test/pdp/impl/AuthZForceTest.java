/**
 * 
 */
package com.thalesgroup.test.pdp.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.Test;

import com.thalesgroup.pdp.core.AuthZForceImpl;

/**
 * @author romain.ferrari[AT]thalesgroup.com
 *
 */
public class AuthZForceTest extends TestCase {
	
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(AuthZForceTest.class);

	/**
	 * Test method for {@link com.thalesgroup.pdp.core.AuthZForceImpl#pdpXmlGetEval(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPdpXmlGetPermitEval() {
		AuthZForceImpl pdp = new AuthZForceImpl();
		String response = null;
		LOGGER.info("Testing for a PERMIT response");
		response = pdp.pdpXmlGetEval("tcmanager", "t0101841-Virtualbox", "POST");
		if (!response.contains("<Decision>PERMIT</Decision>")) {
			fail("Bad evaluation for PERMIT Response, Response: "+response);
		}
		LOGGER.info("OK");
	}
	/**
	 * Test method for {@link com.thalesgroup.pdp.core.AuthZForceImpl#pdpXmlGetEval(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPdpXmlGetDenyEval() {
		AuthZForceImpl pdp = new AuthZForceImpl();
		String response = null;
		LOGGER.info("Testing for a DENY response");
		response = pdp.pdpXmlGetEval("T0101841", "deny", "POST");
		if (!response.contains("<Decision>DENY</Decision>")) {
			fail("Bad evaluation for DENY Response, Response: "+response);
		}
		LOGGER.info("OK");
	}
	
	/**
	 * Test method for {@link com.thalesgroup.pdp.core.AuthZForceImpl#pdpXmlGetEval(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testPdpXmlGetNotApplicableEval() {
		AuthZForceImpl pdp = new AuthZForceImpl();
		String response = null;
		LOGGER.info("Testing for a NOT_APPLICABLE response");
		response = pdp.pdpXmlGetEval("tcmanager", "t0101841-Virtualbox", "GET");
		if (!response.contains("<Decision>NOT_APPLICABLE</Decision>")) {
			fail("Bad evaluation for NOT_APPLICABLE Response, Response: "+response);
		}
		LOGGER.info("OK");
	}

	/**
	 * Test method for {@link com.thalesgroup.pdp.core.AuthZForceImpl#pdpXmlPostXacmlEval(java.lang.String, com.sun.jersey.api.representation.Form)}.
	 */
	@Test
	public void testPdpXmlPostXacmlEval() {
		LOGGER.info("Testing the POST Evaluation with a Sample request");
		String response = "";
		String request = "";
		BufferedReader lect ;		
		String path = "src/test/resources/XACML_Sample_Request.xml";
		try
		{
			lect = new BufferedReader(new FileReader(path)) ;
			while (lect.ready()==true) 
			{
				request += lect.readLine() ; 
			}//while
		}//try
		catch (NullPointerException a)
		{
			System.out.println("Erreur : pointeur null");
		}
		catch (IOException a) 
		{
			System.out.println("Problème d'IO");
		}		
		LOGGER.info("Sample Request: ");
		LOGGER.info(request);
		AuthZForceImpl pdp = new AuthZForceImpl();
		try {
			response = pdp.pdpXmlPostXacmlEval(request);
		} catch (IOException e) {
			LOGGER.fatal(e.getLocalizedMessage());
		}
		if (!response.contains("<Decision>PERMIT</Decision>")) {
			fail("Bad evaluation for testPdpXmlPostXacmlEval, Response: "+response);
		}
		LOGGER.info("OK");
	}
}
