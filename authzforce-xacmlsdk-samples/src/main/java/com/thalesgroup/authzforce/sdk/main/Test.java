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
package com.thalesgroup.authzforce.sdk.main;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdkImpl;

/**
 * 
 * Test class for standard utilization of this SDK
 * 
 */
public class Test {

	private static final Logger LOGGER = LoggerFactory.getLogger(Test.class);
	
	private static final String PDP_ENDPOINT = "http://testing-iam.cryptex.theresis.org:9400/authzforce-webapp-3.0.0-20131206.090429-7";
	private static final String DOMAIN_ID = "ff88c864-8f1b-11e3-83ed-005056a2287a";

	private static final String SUBJECT = "ThalesId";
	private static final String SUBJECT_2 = "userName";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String RESOURCE_TENANT_ID = "Exo";
	private static final String RESOURCE_2 = "EasiClouds";
	private static final String RESOURCE_2_TENANT_ID = "Thales";
	private static final String ACTION_2 = "OPTION";

	public static void main(String[] args) {
		mainObject();
	}
	
	private static void mainObject() {
		List<Subject> subjects = new ArrayList<Subject>();
		List<Resource> resources = new ArrayList<Resource>();
		List<Action> actions = new ArrayList<Action>();
		
		Subject subject = new Subject(SUBJECT);
		subject.setIncludeInResult(true);
		
		/*
		 * BUG: APPSEC-174
		 */
		Subject subject2 = new Subject(SUBJECT_2);
		subject.setIncludeInResult(true);
		
		subjects.add(subject);
		subjects.add(subject2);
				
		Resource rsc1 = new Resource(RESOURCE);
		Resource rsc2 = new Resource(RESOURCE_TENANT_ID);
		rsc2.setAttributeId("urn:oasis:names:tc:xacml:1.0:resource:tenant-id");
		
		Resource rsc3 = new Resource(RESOURCE_2);		
		Resource rsc4 = new Resource(RESOURCE_2_TENANT_ID);
		rsc4.setAttributeId("urn:oasis:names:tc:xacml:1.0:resource:tenant-id");
		
		rsc1.setIncludeInResult(true);
		rsc2.setIncludeInResult(true);		
		
		
		// The order of the placement is really important. Especially for the resource
		resources.add(rsc1);
		resources.add(rsc2);
		resources.add(rsc3);		
		resources.add(rsc4);
		Integer testInt = 1;
		
//		Action act1 = new Action(ACTION);
		Action act1 = new Action(testInt);
		Action act2 = new Action(ACTION_2);
		act1.setIncludeInResult(true);
		act2.setIncludeInResult(true);
		
		actions.add(act1);
		actions.add(act2);
		
		Environment environment = new Environment("iam-hmi");
		
		XacmlSdk myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT), DOMAIN_ID);

		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subjects, resources, actions, environment);
		} catch (XacmlSdkException e) {
			LOGGER.error(e.getLocalizedMessage());
			LOGGER.error(e.getCause().getLocalizedMessage());
		}
		if(responses != null) {
			for (Response response : responses.getResponse()) {
				LOGGER.info(response.getAction() + " on "
						+ response.getResourceId() + ": " 
						+ response.getDecision().value() + " for " 
						+ response.getSubject());
			}
		}		
	}
}
