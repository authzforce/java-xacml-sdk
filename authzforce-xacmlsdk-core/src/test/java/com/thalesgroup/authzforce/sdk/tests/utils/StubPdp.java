package com.thalesgroup.authzforce.sdk.utils;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.contentType;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.withPostBody;

import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import com.xebialabs.restito.server.StubServer;
import com.xebialabs.restito.support.junit.ServerDependencyRule;

public abstract class StubPdp {
	protected StubServer server;

	@Rule
	public ServerDependencyRule serverDependency = new ServerDependencyRule();
	
	@Before
	public void startServer() throws FileNotFoundException, JAXBException {
		if (serverDependency.isServerDependent()) {
			
		}
	}

	@After
	public void stopServer() {
		if (server != null) {
			server.stop();
		}
	}
}
