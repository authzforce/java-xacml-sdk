package com.thalesgroup.authzforce.sdk;

import java.util.List;
import java.util.Map;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

public interface XacmlSdk {

	Response getAuthZ(Map<XACMLAttributeId, String> mySubject,
			Map<XACMLAttributeId, String> resourceList, Map<XACMLAttributeId, String> myAction,
			Map<XACMLAttributeId, String> myEnvironment) throws XacmlSdkException;

	Responses getAuthZ(Map<XACMLAttributeId, String> mySubject,
			List<Map<XACMLAttributeId, String>> resourceList,
			Map<XACMLAttributeId, String> myAction, Map<XACMLAttributeId, String> myEnvironment)
			throws XacmlSdkException;
	
	Response getAuthZ(Subject subject, Resource resource, Action action, Environment environment)
			throws XacmlSdkException;
	
	Responses getAuthZ(Subject subject, List<Resource> resources, Action action, Environment environment)
			throws XacmlSdkException;
	
	Responses getAuthZ(Subject subject, Resource resource, List<Action> actions, Environment environment)
			throws XacmlSdkException;
	
	Responses getAuthZ(Subject subject, List<Resource> resources, List<Action> actions, Environment environment)
			throws XacmlSdkException;
	
	String toString();

}
