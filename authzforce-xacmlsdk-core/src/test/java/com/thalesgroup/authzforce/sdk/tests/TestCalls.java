package com.thalesgroup.authzforce.sdk.tests;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.contentType;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static com.xebialabs.restito.semantics.Condition.withPostBody;

import java.io.FileNotFoundException;
import java.net.URI;

import javax.xml.bind.JAXBException;

import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.impl.XacmlSdkImpl;
import com.thalesgroup.authzforce.sdk.tests.utils.Utils;
import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.junit.NeedsServer;
import com.xebialabs.restito.support.junit.ServerDependencyRule;

public class TestCalls {

	private static final String USER_DOMAIN = "5e022256-6d0f-4eb8-aa9d-77db3d4ad141";

	protected StubServer server;

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Rule
	public ServerDependencyRule serverDependency = new ServerDependencyRule();

	private static final Logger LOGGER = LoggerFactory.getLogger(TestCalls.class);
	private final static String ENDPOINT_ADDRESS = "http://127.0.0.1:" + StubServer.DEFAULT_PORT + "/";

	private static final String TEST_HEADER_KEY = "X-AUTH-TOKEN";

	@Before
	public void setUp() throws FileNotFoundException, JAXBException {
		if (serverDependency.isServerDependent()) {
			server = new StubServer(StubServer.DEFAULT_PORT).run();
			final String expectedResponse = Utils.printResponse(Utils.createResponse("src/test/resources/responses/simple-response.xml"));
			whenHttp(server).match(withPostBody()).then(ok(), stringContent(expectedResponse), contentType("application/xml"));
		}
	}
	
	@After
	public void stopServer() {
		if (null != server) {
			server.stop();
		}
	}

	/**
	 * This test has be written to check that the SDK is actually throwing
	 * proper exceptions when not finding an authorization server
	 * 
	 * @throws XacmlSdkException
	 */
	@Test
	public void TestPdpNotFound() throws XacmlSdkException {
		LOGGER.info("Testing not responsive server");
		XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN, null);
		exception.expect(XacmlSdkException.class);
		sdk.getAuthZ(new SubjectCategory(), new ResourceCategory(), new ActionCategory(), new EnvironmentCategory());
		Assert.fail("Exception not thrown");

	}

	@Test
	@NeedsServer
	public void TestEmptyRequest() throws XacmlSdkException {
		LOGGER.info("Testing empty request call");
		XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN, null);
		sdk.getAuthZ(new SubjectCategory(), new ResourceCategory(), new ActionCategory(), new EnvironmentCategory());
		verifyHttp(server).once(withPostBody());
	}

	@Test
	@NeedsServer
	public void TestCustomHeaders() throws XacmlSdkException {
		LOGGER.info("Testing custom header funcitonnality");
		MetadataMap<String, String> headers = new MetadataMap<String, String>();
		headers.add(TEST_HEADER_KEY, "TEST-AUTH-TOKEN");
		XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN, headers);
		sdk.getAuthZ(new SubjectCategory(), new ResourceCategory(), new ActionCategory(), new EnvironmentCategory());
		verifyHttp(server).once(withHeader(TEST_HEADER_KEY));
	}
}