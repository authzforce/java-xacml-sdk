package com.thalesgroup.authzforce.sdk.tests;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.ow2.authzforce.sdk.core.Utils;
import org.ow2.authzforce.sdk.core.schema.Action;
import org.ow2.authzforce.sdk.core.schema.Attribute;
import org.ow2.authzforce.sdk.core.schema.Environment;
import org.ow2.authzforce.sdk.core.schema.Request;
import org.ow2.authzforce.sdk.core.schema.Resource;
import org.ow2.authzforce.sdk.core.schema.Response;
import org.ow2.authzforce.sdk.core.schema.Responses;
import org.ow2.authzforce.sdk.core.schema.Subject;
import org.ow2.authzforce.sdk.core.schema.SubjectKey;
import org.ow2.authzforce.sdk.core.schema.SubjectRequestTime;
import org.ow2.authzforce.sdk.core.schema.category.ActionCategory;
import org.ow2.authzforce.sdk.core.schema.category.Category;
import org.ow2.authzforce.sdk.core.schema.category.EnvironmentCategory;
import org.ow2.authzforce.sdk.core.schema.category.ResourceCategory;
import org.ow2.authzforce.sdk.core.schema.category.SubjectCategory;
import org.ow2.authzforce.sdk.core.utils.ResponsesFactory;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
		LOGGER.debug("Actual Request: {}", actualRequest);
		LOGGER.debug("Expected Request: {}", expectedRequest);
		Assert.assertEquals(expectedRequest, actualRequest);
	}

    @Test
    public void TestCreateXacmlRequestMultipleAttributes() throws FileNotFoundException, JAXBException, XacmlSdkException {
        LOGGER.info("Testing Request creation");
        Date now = new Date();
		List<Attribute> expectedAttributes=new ArrayList<>();
		expectedAttributes.add(new Subject("TestCreateXacmlRequest"));
		expectedAttributes.add(new SubjectRequestTime(now));
		expectedAttributes.add(new SubjectKey("aPublicKeyGoesHere"));
		for (Attribute attribute : expectedAttributes) {
			subjectCat.addAttribute(attribute);
		}

        final Request actualRequest = Utils.createXacmlRequest(Arrays.asList(subjectCat), Arrays.asList(resourceCat), Arrays.asList(actionCategory), Arrays.asList(environmentCategory));
        LOGGER.info(actualRequest.toString());

		List<oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute> actualAttributes = ((Category)actualRequest.getAttributes().get(1)).getAttributes();

		for (int i = 0; i < expectedAttributes.size(); i++) {
			Assert.assertEquals(expectedAttributes.get(i).toString(),expectedAttributes.get(i).getAttributeValues(), actualAttributes.get(i).getAttributeValues());
		}
    }

	@Test
	public void TestCreateXhtmlRequestAttributeConstructors() throws XacmlSdkException {
		ByteArrayOutputStream errContent = new ByteArrayOutputStream();
		PrintStream originalErr = System.err;
		System.setErr(new PrintStream(errContent));

		LOGGER.info("Testing Request creation");
		subjectCat.addAttribute(new Subject(1));
		subjectCat.addAttribute(new Subject(1D));
		subjectCat.addAttribute(new Subject(true));
		subjectCat.addAttribute(new SubjectRequestTime(1));
		resourceCat.addAttribute(new Resource(1D));
		resourceCat.addAttribute(new Resource(true));
		actionCategory.addAttribute(new Action(1));
		actionCategory.addAttribute(new Action(1D));
		actionCategory.addAttribute(new Action(true));
		actionCategory.addAttribute(new Action(new Date()));
		environmentCategory.addAttribute(new Environment(1));
		environmentCategory.addAttribute(new Environment(1D));
		environmentCategory.addAttribute(new Environment(true));
		final String actualRequest = Utils.createXacmlRequest(Arrays.asList(subjectCat), Arrays.asList(resourceCat), Arrays.asList(actionCategory), Arrays.asList(environmentCategory)).toString();
		Assert.assertNotNull(actualRequest);
		System.setErr(originalErr);
		LOGGER.debug(errContent.toString());
		Assert.assertFalse("Should not have thrown SAXException2", errContent.toString().contains("SAXException2"));
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
		Response response = actualResponse.getResponses().get(0);
		//note that Collections to String return as [value] instead of just value
		Assert.assertEquals(Collections.singletonList("http://www.opencloudware.org").toString(), response.getResourceId());
		Assert.assertEquals(Collections.singletonList("ThalesId").toString(), response.getSubjectId());
		Assert.assertEquals(Collections.singletonList("HEAD").toString(), response.getActionId());
	}
	
	@Test
	public void TestExtractResponseWithMultipleResponses() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing extractResponse with multiple responses");
		Responses actualResponse = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse("src/test/resources/responses/TestExtractResponseWithMultipleResponses.xml"));
		Assert.assertNotEquals(1, actualResponse.getResponses().size());		
	}
	
	@Test
	public void TestExtractResponseWithMultipleSameSubject() throws FileNotFoundException, JAXBException, XacmlSdkException {
		LOGGER.info("Testing extractResponse multiple identical subjects");
		Responses actualResponse = Utils.extractResponse(com.thalesgroup.authzforce.sdk.tests.utils.Utils.createResponse("src/test/resources/responses/TestExtractResponseWithMultipleSameSubject.xml"));
		Assert.assertNotNull(actualResponse);
	}

//	@Test
//	public void TestCheckCategoryOk() {
//		Assert.fail("To be implemented");
//	}
//
//	@Test
//	public void TestCheckCategoryNotOk() {
//		Assert.fail("To be implemented");
//	}
}
