package com.thalesgroup.authzforce.sdk;

import java.util.List;
import java.util.Map;

import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

public interface XacmlSdk {

	Response getAuthZ(Map<String, String> mySubject,
			Map<String, String> myResource, Map<String, String> myAction,
			Map<String, String> myEnvironment) throws XacmlSdkException;

	Response getAuthZ(Map<String, String> mySubject,
			List<Map<String, String>> resourceList,
			Map<String, String> myAction, Map<String, String> myEnvironment)
			throws XacmlSdkException;
	
	String toString();

}
