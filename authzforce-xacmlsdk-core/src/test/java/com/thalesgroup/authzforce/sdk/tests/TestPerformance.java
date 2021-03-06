package com.thalesgroup.authzforce.sdk.tests;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.contentType;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.withPostBody;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.ow2.authzforce.sdk.core.schema.category.ActionCategory;
import org.ow2.authzforce.sdk.core.schema.category.EnvironmentCategory;
import org.ow2.authzforce.sdk.core.schema.category.ResourceCategory;
import org.ow2.authzforce.sdk.core.schema.category.SubjectCategory;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.ow2.authzforce.sdk.impl.XacmlSdkImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.tests.utils.Utils;
import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.junit.NeedsServer;
import com.xebialabs.restito.support.junit.ServerDependencyRule;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

public class TestPerformance {

	private static final String USER_DOMAIN = "5e022256-6d0f-4eb8-aa9d-77db3d4ad141";

	protected StubServer server;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public ServerDependencyRule serverDependency = new ServerDependencyRule();

	private static final int MYTHREADS = 30;
	// ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);
	ExecutorService executor = new ThreadPoolExecutor(MYTHREADS, MYTHREADS, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<Runnable>(MYTHREADS, true), new ThreadPoolExecutor.CallerRunsPolicy());

	private List<ResourceCategory> myResourceCategory;
	private List<SubjectCategory> mySubjCategroy;
	private List<ActionCategory> myActionCategory;
	private List<EnvironmentCategory> myEnvironmentCategory;

	private final static String ENDPOINT_ADDRESS = "http://127.0.0.1:" + StubServer.DEFAULT_PORT + "/";
	private final static XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN);

	private static final int WARM_UP_ROUNDS = 1000;

	private static final int[] TEST_ROUND = {1, 10, 50, 100, 200, 250, 500, 700, 1000, 1500, 2000, 3000, 4000, 5000, 7000, 10000};

	private static final String SEPARATOR = "\t\t";

	private StringWriter resultsReqS = new StringWriter();

	private StringWriter results = new StringWriter();

	@Before
	public void setUp() throws FileNotFoundException, JAXBException {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
		ch.qos.logback.classic.Logger sdkLogger = loggerContext.getLogger("com.thalesgroup");
		rootLogger.setLevel(Level.OFF);
		sdkLogger.setLevel(Level.OFF);
		if (serverDependency.isServerDependent()) {
			server = new StubServer(StubServer.DEFAULT_PORT).run();
			final String expectedResponse = Utils
					.printResponse(Utils.createResponse("src/test/resources/responses/simple-response.xml"));
			whenHttp(server).match(withPostBody()).then(ok(), stringContent(expectedResponse),
					contentType("application/xml"));
		}
		categorySetUp();
	}

	private void categorySetUp() {
		mySubjCategroy = Arrays.asList(new SubjectCategory());
		myResourceCategory = Arrays.asList(new ResourceCategory());
		myActionCategory = Arrays.asList(new ActionCategory());
		myEnvironmentCategory = Arrays.asList(new EnvironmentCategory());
	}

	@After
	public void stopServer() {
		System.out.println(results.toString());
		System.out.println();
		System.out.println(resultsReqS.toString());
		if (null != server) {
			server.stop();
		}
	}

	private void warmUp() {
		System.out.println("Warming up the JVM....");
		XacmlSdkImpl sdk = new XacmlSdkImpl(URI.create(ENDPOINT_ADDRESS), USER_DOMAIN, null);
		for (int i = 0; i < WARM_UP_ROUNDS; i++) {
			try {
				sdk.getAuthZ(mySubjCategroy, myResourceCategory, myActionCategory, myEnvironmentCategory);
			} catch (XacmlSdkException e) {
				e.printStackTrace();
			}
			// verifyHttp(server).once(withPostBody());
		}
	}

	private void TestMultipleRequests(int nbRequest) throws XacmlSdkException {
		long before = System.nanoTime();
		for (int i = 0; i < nbRequest; i++) {
			executor.execute(new Runnable() {
				public void run() {
					try {
						sdk.getAuthZ(mySubjCategroy, myResourceCategory, myActionCategory, myEnvironmentCategory)
								.getResponses().get(0).getDecision();
					} catch (XacmlSdkException e) {
						e.printStackTrace();
					}
				}
			});
		}

		long processingTime = System.nanoTime();
		processingTime -= before;
		long requestBySec = Long.valueOf(nbRequest)/processingTime;
		processingTime = processingTime/Long.valueOf(nbRequest);
		results.append(processingTime + "\n");
		resultsReqS.append(requestBySec +"\n");
	}

	@Test
	@NeedsServer
	public void TestSDKPerformance() throws XacmlSdkException {
		warmUp();
		System.out.println("Starting tests");
		for (int round : TEST_ROUND) {
			// System.out.println("Round #"+round);
			TestMultipleRequests(round);
		}
		executor.shutdown();
		try {
			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				// pool didn't terminate after the first try
				executor.shutdownNow();
			}

			if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
				// pool didn't terminate after the second try
			}
		} catch (InterruptedException ex) {
			executor.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
