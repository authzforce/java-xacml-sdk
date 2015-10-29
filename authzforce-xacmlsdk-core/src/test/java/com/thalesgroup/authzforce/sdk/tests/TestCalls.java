package com.thalesgroup.authzforce.sdk.xacml.tests;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.impl.XacmlSdkImpl;

public class TestPdpNetworkErrors {

	private static final String USER_DOMAIN = "5e022256-6d0f-4eb8-aa9d-77db3d4ad141";

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestPdpNetworkErrors.class);
	private final static String ENDPOINT_ADDRESS = "http://127.0.0.1:7777/";
	public final XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN, null);
	


	/**
	 * This test has be written to check that the SDK is actually throwing
	 * proper exceptions when not finding an authorization server
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestPdpNotFound() throws XacmlSdkException {
		exception.expect(XacmlSdkException.class);
		sdk.getAuthZ(new SubjectCategory(), new ResourceCategory(),
				new ActionCategory(), new EnvironmentCategory());
		Assert.fail("Exception not thrown");

	}
	
	/**
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestGetAuthZ_actionList() throws XacmlSdkException {		
	}
	
	/**
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestGetAuthZ_resourcesList() throws XacmlSdkException {
	}
}