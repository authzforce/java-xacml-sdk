package com.thalesgroup.authzforce.sdk.xacml.tests;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdkImpl;

public class TestPdpNetworkErrors {

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	public final XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create("http://127.0.0.1/pdp"),
			"default");

	/**
	 * This test has be written to check that the SDK is actually throwing
	 * proper exceptions when not finding an authorization server
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestPdpNotFound() throws XacmlSdkException {		

		exception.expect(XacmlSdkException.class);
		sdk.getAuthZ(new Subject("subjectId"), new Resource("resourceId"),
				new Action("actionId"), new Environment("environmentId"));
		Assert.fail("Exception not thrown");

	}
	
	/**
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestGetAuthZ_actionList() throws XacmlSdkException {
		List<Action> actionList = new ArrayList<Action>();
		actionList.add(new Action("actionId"));
		actionList.add(new Action("secondAction"));
		
		Responses response = sdk.getAuthZ(new Subject("subjectId") , new Resource("resourceId"),
				actionList, new Environment("environmentId"));
	}
	
	/**
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestGetAuthZ_resourcesList() throws XacmlSdkException {
		List<Resource> resourcesList = new ArrayList<Resource>();
		resourcesList.add(new Resource("actionId"));
		resourcesList.add(new Resource("secondAction"));
		
		Responses response = sdk.getAuthZ(new Subject("subjectId") , resourcesList,
				new Action("actionId"), new Environment("environmentId"));
	}
}