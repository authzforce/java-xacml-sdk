package com.thalesgroup.authzforce.sdk.tests;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.Utils;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

public class TestResponseFactory {
	private static final Logger LOGGER = LoggerFactory.getLogger(TestResponseFactory.class);
	private static final String REQUEST_FILES_PATH = "src/test/resources/requests";
	private static final String RESPONSE_FILES_PATH = "src/test/resources/responses";

	@Test
	public void testGetResponseGroupBySubject() throws FileNotFoundException, JAXBException, XacmlSdkException {
		Responses responses = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse(RESPONSE_FILES_PATH + "/TestResponseFactoryGetResponseGroupBySubject.xml")).getResponseGroupBySubject();
		for (Response response : responses.getResponses()) {
			LOGGER.debug(response.getSubjectId());
			LOGGER.debug(response.getActionId());
			LOGGER.debug(response.getResourceId());
			LOGGER.debug(response.getDecision().value());
		}
		Assert.fail("Not implemented");
	}
	
	@Test
	public void testGetResponseGroupByResource() {
		Assert.fail("Not implemented");
	}
	
	@Test
	public void testGetResponseGroupByAction() {
		Assert.fail("Not implemented");
	}
}
