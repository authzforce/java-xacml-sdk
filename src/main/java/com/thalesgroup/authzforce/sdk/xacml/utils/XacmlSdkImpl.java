/**
 * Copyright (C) 2013-2013 Thales Services - ThereSIS - All rights reserved.
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
package com.thalesgroup.authzforce.sdk.xacml.utils;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResponseType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.ResultType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkExceptionCodes;

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

	private RequestType request;
	private WebResource webResource;
	private Client client;

//	private List<AttributesType> attributes = new LinkedList<AttributesType>();
	private List<AttributesType> resourceCategory = new LinkedList<AttributesType>();
	private List<AttributesType> actionCategory = new LinkedList<AttributesType>();
	private List<AttributesType> subjectCategory = new LinkedList<AttributesType>();
	private List<AttributesType> environmentCategory = new LinkedList<AttributesType>();
	
	private RequestType myRequest;

	/**
	 * Constructor
	 * 
	 * @param serverEndpoint
	 */
	public XacmlSdkImpl(URI serverEndpoint) {
		this.client = new Client();
		this.webResource = this.client.resource(serverEndpoint);
		this.webResource.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING, false);
	}

	private void clearRequest() {
		this.request = new Request();
		myRequest = new RequestType();
		resourceCategory.clear();
		actionCategory.clear();
		subjectCategory.clear();
		environmentCategory.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#toString()
	 */
	@Override
	public String toString() {
		LOGGER.debug("Create XML (marshalling)");
		java.io.StringWriter sw = new StringWriter();
		try {
			Marshaller marsh = JAXBContext.newInstance(RequestType.class)
					.createMarshaller();
			marsh.marshal(new JAXBElement<RequestType>(new QName(
					"urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"),
					RequestType.class, request), sw);
		} catch (JAXBException e) {
			LOGGER.error("", e);
		}
		LOGGER.debug(new String(sw.getBuffer()).replaceAll("\"", "'"));

		return sw.toString();
	}

	private void forgeResource(Resource resource) throws XacmlSdkException {
		if (resource != null) {
			AttributesType attr = new AttributesType();
			AttributeType attrId = new AttributeType();
			LOGGER.debug("Forging Resource...");
			if (resourceCategory.size() > 0) {
				if (resource.getAttributeId().equals(
						XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
					boolean containId = false;
					for (AttributesType attrsType : resourceCategory) {
						for (AttributeType attrType : attrsType.getAttribute()) {
							if (attrType.getAttributeId().equals(
									XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID
											.value())) {
								containId = true;
								attrId = attrType;
								break;
							}
						}
						if (containId) {
							break;
						}
					}
					if (containId) {
						if (!attrId.equals(resource)) {
							attr.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE
									.value());
							attr.getAttribute().add(resource);
							resourceCategory.add(attr);
						}
					} else {
						resourceCategory.get(resourceCategory.size() - 1)
								.getAttribute().add(resource);
					}
				} else {
					resourceCategory.get(resourceCategory.size() - 1)
							.getAttribute().add(resource);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE
						.value());
				attr.getAttribute().add(resource);
				resourceCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_RESOURCE.value());
		}
	}

	private void forgeSubject(Subject subject) throws XacmlSdkException {
		if (subject != null) {
			AttributesType attr = new AttributesType();
			AttributeType attrId = new AttributeType();
			LOGGER.debug("Forging Subject...");
			if (subjectCategory.size() > 0) {
				if (subject.getAttributeId().equals(
						XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
					boolean containId = false;
					for (AttributesType attrsType : subjectCategory) {
						for (AttributeType attrType : attrsType.getAttribute()) {
							if (attrType.getAttributeId().equals(
									XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID
											.value())) {
								containId = true;
								attrId = attrType;
								break;
							}
						}
						if (containId) {
							break;
						}
					}
					if (containId) {
						if (!attrId.equals(subject)) {
							attr.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT
									.value());
							attr.getAttribute().add(subject);
							subjectCategory.add(attr);
						}
					} else {
						subjectCategory.get(subjectCategory.size() - 1)
								.getAttribute().add(subject);
					}
				} else {
					subjectCategory.get(subjectCategory.size() - 1)
							.getAttribute().add(subject);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT
						.value());
				attr.getAttribute().add(subject);
				subjectCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_SUBJECT.value());
		}
	}

	private void forgeAction(Action action) throws XacmlSdkException {
		if (action != null) {
			AttributesType attr = new AttributesType();
			AttributeType attrId = new AttributeType();
			LOGGER.debug("Forging Action...");
			if (actionCategory.size() > 0) {
				if (action.getAttributeId().equals(
						XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
					boolean containId = false;
					for (AttributesType attrsType : actionCategory) {
						for (AttributeType attrType : attrsType.getAttribute()) {
							if (attrType.getAttributeId().equals(
									XACMLAttributeId.XACML_ACTION_ACTION_ID
											.value())) {
								containId = true;
								attrId = attrType;
								break;
							}
						}
						if (containId) {
							break;
						}
					}
					if (containId) {
						if (!attrId.equals(action)) {
							attr.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION
									.value());
							attr.getAttribute().add(action);
							actionCategory.add(attr);
						}
					} else {
						actionCategory.get(actionCategory.size() - 1)
								.getAttribute().add(action);
					}
				} else {
					actionCategory.get(actionCategory.size() - 1)
							.getAttribute().add(action);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION
						.value());
				attr.getAttribute().add(action);
				actionCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ACTION.value());
		}
	}

	private void forgeEnvironment(Environment environment)
			throws XacmlSdkException {
		if (environment != null) {
			AttributesType attr = new AttributesType();
			AttributeType attrId = new AttributeType();
			LOGGER.debug("Forging Environment...");
			if (environmentCategory.size() > 0) {
				if (environment.getAttributeId().equals(
						XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID
								.value())) {
					boolean containId = false;
					for (AttributesType attrsType : environmentCategory) {
						for (AttributeType attrType : attrsType.getAttribute()) {
							if (attrType
									.getAttributeId()
									.equals(XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID
											.value())) {
								containId = true;
								attrId = attrType;
								break;
							}
						}
						if (containId) {
							break;
						}
					}
					if (containId) {
						if (!attrId.equals(environment)) {
							attr.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT
									.value());
							attr.getAttribute().add(environment);
							environmentCategory.add(attr);
						}
					} else {
						environmentCategory.get(environmentCategory.size() - 1)
								.getAttribute().add(environment);
					}
				} else {
					environmentCategory.get(environmentCategory.size() - 1)
							.getAttribute().add(environment);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT
						.value());
				attr.getAttribute().add(environment);
				environmentCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ENVIRONMENT.value());
		}
	}

	private RequestType createXacmlRequest(Subject subject,
			List<Resource> resources, List<Action> actions,
			Environment environment) {
		RequestType xacmlRequest = new RequestType();

		LOGGER.debug("Assembling XACML...");
		// subjectCategory.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT
		// .value());
		// resourcesCategory.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE
		// .value());
		// actionCategory.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION
		// .value());
		// environmentCategory.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT
		// .value());
		try {
			forgeSubject(subject);
			forgeEnvironment(environment);
			for (Action action : actions) {
				forgeAction(action);
			}
			for (Resource resource : resources) {
				forgeResource(resource);
			}
		} catch (XacmlSdkException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// xacmlRequest.getAttributes().addAll(attributes);
		xacmlRequest.getAttributes().addAll(subjectCategory);
		xacmlRequest.getAttributes().addAll(resourceCategory);
		xacmlRequest.getAttributes().addAll(actionCategory);
		xacmlRequest.getAttributes().addAll(environmentCategory);
		xacmlRequest.setCombinedDecision(false);
		xacmlRequest.setReturnPolicyIdList(false);

		this.request = xacmlRequest;
//		StringWriter writer = new StringWriter();
//		try {
//			JAXBContext jc = JAXBContext
//					.newInstance("oasis.names.tc.xacml._3_0.core.schema.wd_17");
//			Marshaller u = jc.createMarshaller();
//			u.marshal(xacmlRequest, writer);
//			/* Doing some debugging log at least */
//			LOGGER.debug(writer.toString());
//		} catch (Exception e) {
//			System.out.println(e);
//		}
//		System.out.println(writer);

		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup
	 * .authzforce.sdk.core.schema.Subject, java.util.List, java.util.List,
	 * com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	@Override
	public Responses getAuthZ(Subject subject, List<Resource> resources,
			List<Action> actions, Environment environment)
			throws XacmlSdkException {
		Responses responses = new Responses();
		/*
		 * FIXME: Loop to handle some kind of xacml v3.0 emulation
		 */
		myRequest = createXacmlRequest(subject, resources, actions,
				environment);
		StringWriter writer = new StringWriter();
		try {
			JAXBContext jc = JAXBContext
					.newInstance("oasis.names.tc.xacml._3_0.core.schema.wd_17");
			Marshaller u = jc.createMarshaller();
			u.marshal(myRequest, writer);
			/* Doing some debugging log at least */
//			LOGGER.debug(writer.toString());
		} catch (Exception e) {
			System.out.println(e);
		}
		ResponseType myResponse = webResource
				.type(MediaType.APPLICATION_XML)
				.post(ResponseType.class, writer.toString());

		// FIXME: possible NPE on each of the getContent
		for (ResultType result : myResponse.getResult()) {
			Response response = new Response();
			for (AttributesType returnedAttr : result.getAttributes()) {
				for (AttributeType attr : returnedAttr.getAttribute()) {
					if (attr.getAttributeId()
							.equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID
									.value())) {
						for (AttributeValueType attrValue : attr
								.getAttributeValue()) {
							response.setResourceId(String.valueOf(attrValue
									.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(
							XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
						for (AttributeValueType attrValue : attr
								.getAttributeValue()) {
							response.setAction(String.valueOf(attrValue
									.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(
							XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
						for (AttributeValueType attrValue : attr
								.getAttributeValue()) {
							response.setSubject(String.valueOf(attrValue
									.getContent().get(0)));
						}
					}
				}
			}
			response.setDecision(result.getDecision());
			responses.getResponse().add(response);
			
			this.clearRequest();
		}
		return responses;
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
	@Override
	public Response getAuthZ(Subject subject, Resource resource, Action action,
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
	@Override
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
	@Override
	public Responses getAuthZ(Subject subject, Resource resource,
			List<Action> action, Environment environment)
			throws XacmlSdkException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		tmpResourceList.add(resource);

		return getAuthZ(subject, tmpResourceList, action, environment);
	}

}
