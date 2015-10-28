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
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(XacmlSdkImpl.class);

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
	
	public XacmlSdkImpl(URI serverEndpoint, String domainId) {
		networkHandler = new Net(serverEndpoint, domainId, null);
	}

	public Responses getAuthZ(List<SubjectCategory> subject, List<ResourceCategory> resources,
			List<ActionCategory> actions, List<EnvironmentCategory> environment) throws XacmlSdkException {
		// XACML Request creation
		Request request = Utils.createXacmlRequest(subject, resources, actions, environment);
		Response rawResponse = null;
		try {
			LOGGER.debug("Calling PDP using network handler: {}", networkHandler);
			rawResponse = networkHandler.getMyDomain().getPdp().requestPolicyDecision(request);
		} catch (javax.ws.rs.NotFoundException e) {
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
	
	public Responses getAuthZ(SubjectCategory subject, ResourceCategory resources, ActionCategory actions,
			EnvironmentCategory environment) throws XacmlSdkException {
		return this.getAuthZ(Arrays.asList(subject), Arrays.asList(resources), Arrays.asList(actions), Arrays.asList(environment));
	}

	public Responses getAuthZ(List<SubjectCategory> subject, ResourceCategory resources, ActionCategory actions,
			EnvironmentCategory environment) throws XacmlSdkException {
		return this.getAuthZ(subject, Arrays.asList(resources), Arrays.asList(actions), Arrays.asList(environment));
	}

	public Responses getAuthZ(SubjectCategory subject, List<ResourceCategory> resources, ActionCategory actions,
			EnvironmentCategory environment) throws XacmlSdkException {
		return this.getAuthZ(Arrays.asList(subject), resources, Arrays.asList(actions), Arrays.asList(environment));
	}

	public Responses getAuthZ(SubjectCategory subject, ResourceCategory resources, List<ActionCategory> actions,
			EnvironmentCategory environment) throws XacmlSdkException {
		return this.getAuthZ(Arrays.asList(subject), Arrays.asList(resources), actions, Arrays.asList(environment));
	}

	public Responses getAuthZ(SubjectCategory subject, ResourceCategory resources, ActionCategory actions,
			List<EnvironmentCategory> environment) throws XacmlSdkException {
		return this.getAuthZ(Arrays.asList(subject), Arrays.asList(resources), Arrays.asList(actions), environment);
	}
}
