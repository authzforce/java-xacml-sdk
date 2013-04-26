package com.thalesgroup.authzforce.sdk.main;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLDatatypes;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdkImpl;

/**
 * 
 * Test class for standard utilization of this SDK
 * 
 */
public class Test {

	private static final String PDP_ENDPOINT = "http://127.0.0.1:8080/authzforce-rest-interface-3.0.0/service";

	private static final String SUBJECT = "T0101841";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String RESOURCE_2 = "Download";
	private static final String ACTION = "HEAD";
	private static final String ACTION_2 = "OPTION";

	public static void main(String[] args) {
		mainObject();
	}
	
	private static void mainObject() {
		Subject subject = new Subject(SUBJECT, XACMLDatatypes.XACML_DATATYPE_STRING);
		Environment environment = new Environment("", XACMLDatatypes.XACML_DATATYPE_STRING);
		
		List<Resource> resources = new ArrayList<Resource>();
		List<Action> actions = new ArrayList<Action>();
		
		resources.add(new Resource(RESOURCE, XACMLDatatypes.XACML_DATATYPE_STRING));
		resources.add(new Resource(RESOURCE_2, XACMLDatatypes.XACML_DATATYPE_STRING));
		
		actions.add(new Action(ACTION, XACMLDatatypes.XACML_DATATYPE_STRING));
		actions.add(new Action(ACTION_2, XACMLDatatypes.XACML_DATATYPE_STRING));
		
		XacmlSdk myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT));

		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subject, resources, actions, environment);
		} catch (XacmlSdkException e) {
			System.err.println(e);
		}
		for (Response response : responses.getResponse()) {
			System.out.println(response.getAction() + " on "
					+ response.getResourceId() + ": " 
					+ response.getDecision().value() + " for " 
					+ response.getSubject());
		}
	}
}
