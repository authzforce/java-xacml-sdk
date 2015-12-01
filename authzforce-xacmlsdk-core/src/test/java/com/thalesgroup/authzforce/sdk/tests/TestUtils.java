package com.thalesgroup.authzforce.sdk.tests;

import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.Utils;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.core.utils.ResponsesFactory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

public class TestUtils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestUtils.class);
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	SubjectCategory subjectCat = new SubjectCategory();
	ResourceCategory resourceCat = new ResourceCategory();
	ActionCategory actionCategory = new ActionCategory();
	EnvironmentCategory environmentCategory = new EnvironmentCategory();
	
	@Test
	public void TestCreateXacmlRequest() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing Request creation");
		subjectCat.addAttribute(new Subject("TestCreateXacmlRequest"));
		resourceCat.addAttribute(new Resource("TestCreateXacmlRequest"));
		actionCategory.addAttribute(new Action("TestCreateXacmlRequest"));
		environmentCategory.addAttribute(new Environment("TestCreateXacmlRequest"));
		final String actualRequest = Utils.createXacmlRequest(Arrays.asList(subjectCat), Arrays.asList(resourceCat), Arrays.asList(actionCategory), Arrays.asList(environmentCategory)).toString();
		final String expectedRequest = com.thalesgroup.authzforce.sdk.tests.utils.Utils.printRequest(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createRequest("src/test/resources/requests/simple-request.xml"));
		Assert.assertEquals(expectedRequest, actualRequest);
	}
	
	@Test
	public void TestCreateXacmlRequestWithNullCategory() throws XacmlSdkException {
		LOGGER.info("Testing Request creation when a null category ");
		exception.expect(XacmlSdkException.class);
		subjectCat.addAttribute(new Subject(""));
		resourceCat.addAttribute(new Resource(""));		
		environmentCategory.addAttribute(new Environment("TEST_SimpleAuthorizationRequest"));
		Utils.createXacmlRequest(Arrays.asList(subjectCat), Arrays.asList(resourceCat), null, Arrays.asList(environmentCategory));
		Assert.fail("Exception not thrown");
	}

	@Test
	public void TestExtractResponseWithOneResponse() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing extractResponse with only one response");
		ResponsesFactory actualResponse = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse("src/test/resources/responses/TestExtractResponseWithOneResponse.xml"));
		Assert.assertEquals(1, actualResponse.getResponses().size());
	}
	
	@Test
	public void TestExtractResponseWithMultipleResponses() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing extractResponse with multiple responses");
		Responses actualResponse = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse("src/test/resources/responses/TestExtractResponseWithMultipleResponses.xml"));
		Assert.fail("To be implemented");
	}
	
	@Test
	public void TestExtractResponseWithMultipleSameSubject() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing extractResponse multiple identical subjects");
		Responses actualResponse = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse("src/test/resources/responses/TestExtractResponseWithMultipleSameSubject.xml"));
		Assert.fail("To be implemented");
	}

	@Test
	public void TestCheckCategoryOk() {
		Assert.fail("To be implemented");
	}

	@Test
	public void TestCheckCategoryNotOk() {
		Assert.fail("To be implemented");
	}
}
