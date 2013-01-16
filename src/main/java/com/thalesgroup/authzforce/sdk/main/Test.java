package com.thalesgroup.authzforce.sdk.main;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdkImpl;

/**
 *
 *	Test class for standard utilization of this SDK 
 * 
 */
public class Test {
	
	private static final String PDP_ENDPOINT = "http://127.0.0.1:8080/tazs-rest-0.0.1-SNAPSHOT/service";
	
	private static final String SUBJECT = "T0101841";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String RESOURCE_2 = "Download";
	private static final String ACTION = "HEAD";

	public static void main(String[] args) {
		List<Map<String, String>> resourceList;
		
		Map<String, String> mySubject = null;
		Map<String, String> myResource = null;
		Map<String, String> myResource2 = null;
		Map<String, String> myAction = null;
		Map<String, String> myEnvironment = null;
		
		resourceList = new ArrayList<Map<String, String>>();
		
		mySubject = new HashMap<String, String>();
		myResource = new HashMap<String, String>();
		myResource2 = new HashMap<String, String>();
		myAction = new HashMap<String, String>();
		myEnvironment = new HashMap<String, String>();
		
		mySubject.put(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID, SUBJECT);
		myResource.put(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID, RESOURCE);
		myResource2.put(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID, RESOURCE_2);
		myAction.put(XACMLAttributeId.XACML_ACTION_ACTION_ID, ACTION);
		myEnvironment.put(XACMLAttributeId.XACML_SUBJECT_AUTHENTICATION_TIME, new Date().toString());
		
		resourceList.add(myResource);
		resourceList.add(myResource2);
		
		XacmlSdk myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT));

		Response response = null;
		try {
			response = myXacml.getAuthZ(mySubject, resourceList, myAction, myEnvironment);
		} catch (XacmlSdkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (Entry<String, String> map : response.getResponses().entrySet()) {
			System.out.println(map.getKey() + ": " +map.getValue());
		}
	}

}
