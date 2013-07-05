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
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLDatatypes;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdkImpl;

/**
 * 
 * Test class for standard utilization of this SDK
 * 
 */
public class Test {

	private static final String PDP_ENDPOINT = "http://pdp.beeasi.theresis.org:8080/PDP-3.0.0/service";

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
		subject.setIncludeInResult(true);
		Environment environment = new Environment("", XACMLDatatypes.XACML_DATATYPE_STRING);
		
		List<Resource> resources = new ArrayList<Resource>();
		List<Action> actions = new ArrayList<Action>();
		Resource rsc1 = new Resource(RESOURCE, XACMLDatatypes.XACML_DATATYPE_STRING);
		Resource rsc2 = new Resource(RESOURCE_2, XACMLDatatypes.XACML_DATATYPE_STRING);
		Resource rsc3 = new Resource(RESOURCE_2, XACMLDatatypes.XACML_DATATYPE_STRING);
		rsc1.setIncludeInResult(true);
		rsc2.setIncludeInResult(true);
		rsc3.setAttributeId("urn:oasis:names:tc:xacml:1.0:resource:tenant-id");
		
		resources.add(rsc1);
		resources.add(rsc1);
		resources.add(rsc2);
		resources.add(rsc3);
		
		Action act1 = new Action(ACTION, XACMLDatatypes.XACML_DATATYPE_STRING);
		Action act2 = new Action(ACTION_2, XACMLDatatypes.XACML_DATATYPE_STRING);
		act1.setIncludeInResult(true);
		act2.setIncludeInResult(true);
		
		actions.add(act1);
		actions.add(act2);
		
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
