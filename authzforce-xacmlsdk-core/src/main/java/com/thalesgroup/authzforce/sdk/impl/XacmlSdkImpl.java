/**
 * Copyright (C) 2013-2014 Thales Services - ThereSIS - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.thalesgroup.authzforce.sdk.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.Net;
import com.thalesgroup.authzforce.sdk.core.Utils;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;

/**
 * This Library is about XACML and XML Processing tools to make the developers'
 * life easier.
 * 
 * @author Romain FERRARI, romain.ferrari[AT]thalesgroup.com
 * @version 0.5
 * 
 */
public class XacmlSdkImpl implements XacmlSdk {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(XacmlSdkImpl.class);
	
	public final Net networkHandler;

	/**
	 * This constructor is multi tenant enabled. The final endpoint will be
	 * something like: http://serverEndpoint/domains/{domainId}/pdp
	 * 
	 * @param serverEndpoint
	 *            is the PDP endpoint
	 * @param domainId
	 *            is the domain that you belong to
	 */
	public XacmlSdkImpl(URI serverEndpoint, String domainId, MultivaluedMap<String, String> customHeaders) {
		networkHandler = new Net(serverEndpoint, domainId, customHeaders);
	}

	public Responses getAuthZ(List<Subject> subject, List<Resource> resources,
			List<Action> actions, List<Environment> environment)
			throws XacmlSdkException {

		
		// XACML Request creation
		Request request = Utils.createXacmlRequest(subject, resources, actions, environment);
		// Get your domain's resource
		Response rawResponse = null;
		try {
			LOGGER.debug("Calling PDP using network handler: {}", networkHandler);
			rawResponse = networkHandler.getMyDomain().getPdp().requestPolicyDecision(request);
		} catch(javax.ws.rs.NotFoundException e) {
			throw new XacmlSdkException("HTTP 404: Authorization server not found", e);
		} catch (javax.ws.rs.BadRequestException e) {
			throw new XacmlSdkException("HTTP 400: Bad Request", e);
		} catch (javax.ws.rs.InternalServerErrorException e) {
			throw new XacmlSdkException("HTTP 500: Internal Server Error", e);
		} catch (javax.ws.rs.ServerErrorException e) {
			throw new XacmlSdkException(e);
		}		
		if (LOGGER.isDebugEnabled()) {
			Utils.logRawResponse(rawResponse);	
		}
		
		return Utils.extractResponse(rawResponse);
	}
	
	public Responses getAuthZ(List<Subject> subject, List<Resource> resources,
			List<Action> actions, Environment environment)
			throws XacmlSdkException {
		return getAuthZ(subject, resources, actions, Arrays.asList(environment));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup
	 * .authzforce.sdk.core.schema.Subject, java.util.List, java.util.List,
	 * com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	public Responses getAuthZ(Subject subject, List<Resource> resources,
			List<Action> actions, Environment environment)
			throws XacmlSdkException {
		List<Subject> tmpSubjectList = new ArrayList<Subject>();
		tmpSubjectList.add(subject);

		return getAuthZ(tmpSubjectList, resources, actions, environment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup
	 * .authzforce.sdk.core.schema.Subject,
	 * com.thalesgroup.authzforce.sdk.core.schema.Resource,
	 * com.thalesgroup.authzforce.sdk.core.schema.Action,
	 * com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	public com.thalesgroup.authzforce.sdk.core.schema.Response getAuthZ(
			Subject subject, Resource resource, Action action,
			Environment environment) throws XacmlSdkException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		List<Action> tmpActionList = new ArrayList<Action>();
		tmpResourceList.add(resource);
		tmpActionList.add(action);

		return getAuthZ(subject, tmpResourceList, tmpActionList, environment)
				.getResponse().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup
	 * .authzforce.sdk.core.schema.Subject, java.util.List,
	 * com.thalesgroup.authzforce.sdk.core.schema.Action,
	 * com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	public Responses getAuthZ(Subject subject, List<Resource> resource,
			Action action, Environment environment) throws XacmlSdkException {
		List<Action> tmpActionList = new ArrayList<Action>();
		tmpActionList.add(action);

		return getAuthZ(subject, resource, tmpActionList, environment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup
	 * .authzforce.sdk.core.schema.Subject,
	 * com.thalesgroup.authzforce.sdk.core.schema.Resource, java.util.List,
	 * com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	public Responses getAuthZ(Subject subject, Resource resource,
			List<Action> action, Environment environment)
			throws XacmlSdkException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		tmpResourceList.add(resource);

		return getAuthZ(subject, tmpResourceList, action, environment);
	}
}
