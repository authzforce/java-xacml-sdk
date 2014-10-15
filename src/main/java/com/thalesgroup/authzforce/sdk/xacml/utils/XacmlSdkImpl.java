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
package com.thalesgroup.authzforce.sdk.xacml.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.thalesgroup.authzforce.sdk.XacmlSdk;
import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(XacmlSdkImpl.class);
	final static Logger loggerPerf = LoggerFactory.getLogger("perf");

	private Request request;
	private WebResource webResource;
	final static Client CLIENT = new Client();

	private List<Attributes> resourceCategory = new LinkedList<Attributes>();
	private List<Attributes> actionCategory = new LinkedList<Attributes>();
	private List<Attributes> subjectCategory = new LinkedList<Attributes>();
	private List<Attributes> environmentCategory = new LinkedList<Attributes>();
	
	final static JAXBContext JAXBCONTEXT;
	static {try {
		JAXBCONTEXT = JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class);
	} catch (JAXBException e) {
		LOGGER.error("An exception occured during initialization of jaxbcontext", e);
		throw new RuntimeException(e);
	}}
	
	private oasis.names.tc.xacml._3_0.core.schema.wd_17.Request myRequest;

	/**
	 * Constructor
	 * 
	 * @param serverEndpoint
	 */
	public XacmlSdkImpl(URI serverEndpoint) {
//		this.client = new Client();
		this.webResource =CLIENT.resource(serverEndpoint);
		this.webResource.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING, false);
	}

	private void clearRequest() {
		this.request = new Request();
		myRequest = new Request();
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
		StringWriter sw = new StringWriter();
		/*AUTHZFORCE-62 -JAXBContext is initialized at each request. This causes major performance issue*/
		//try {
		//	JAXBContext.newInstance(Request.class).createMarshaller().marshal(request, sw);
		//} catch (JAXBException e) {
		//	LOGGER.error("", e);
		//}
		try {
			JAXBCONTEXT.createMarshaller().marshal(request, sw);
		} catch (JAXBException e1) {
			LOGGER.error(e1.getLocalizedMessage());
		}
		LOGGER.debug(new String(sw.getBuffer()).replaceAll("\"", "'"));

		return sw.toString();
	}

	private void forgeResource(Resource resource) throws XacmlSdkException {
		if (resource != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Resource...");
			if (resourceCategory.size() > 0) {
				if (resource.getAttributeId().equals(
						XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : resourceCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
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
							attr.getAttributes().add(resource);
							resourceCategory.add(attr);
						}
					} else {
						resourceCategory.get(resourceCategory.size() - 1)
								.getAttributes().add(resource);
					}
				} else {
					resourceCategory.get(resourceCategory.size() - 1)
							.getAttributes().add(resource);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE
						.value());
				attr.getAttributes().add(resource);
				resourceCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_RESOURCE.value());
		}
	}

	private void forgeSubject(Subject subject) throws XacmlSdkException {
		if (subject != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Subject...");
			if (subjectCategory.size() > 0) {
				if (subject.getAttributeId().equals(
						XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : subjectCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
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
							attr.getAttributes().add(subject);
							subjectCategory.add(attr);
						}
					} else {
						subjectCategory.get(subjectCategory.size() - 1)
								.getAttributes().add(subject);
					}
				} else {
					subjectCategory.get(subjectCategory.size() - 1)
							.getAttributes().add(subject);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT
						.value());
				attr.getAttributes().add(subject);
				subjectCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_SUBJECT.value());
		}
	}

	private void forgeAction(Action action) throws XacmlSdkException {
		if (action != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Action...");
			if (actionCategory.size() > 0) {
				if (action.getAttributeId().equals(
						XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : actionCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
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
							attr.getAttributes().add(action);
							actionCategory.add(attr);
						}
					} else {
						actionCategory.get(actionCategory.size() - 1)
								.getAttributes().add(action);
					}
				} else {
					actionCategory.get(actionCategory.size() - 1)
							.getAttributes().add(action);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION
						.value());
				attr.getAttributes().add(action);
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
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Environment...");
			if (environmentCategory.size() > 0) {
				if (environment.getAttributeId().equals(
						XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID
								.value())) {
					boolean containId = false;
					for (Attributes attrsType : environmentCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
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
							attr.getAttributes().add(environment);
							environmentCategory.add(attr);
						}
					} else {
						environmentCategory.get(environmentCategory.size() - 1)
								.getAttributes().add(environment);
					}
				} else {
					environmentCategory.get(environmentCategory.size() - 1)
							.getAttributes().add(environment);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT
						.value());
				attr.getAttributes().add(environment);
				environmentCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ENVIRONMENT.value());
		}
	}
	
	/**
	 * 
	 * @param subjects
	 * @param resources
	 * @param actions
	 * @param environment
	 * 
	 * @return
	 */
	private Request createXacmlRequest(List<Subject> subjects,
			List<Resource> resources, List<Action> actions,
			Environment environment) {
		Request xacmlRequest = new Request();

		LOGGER.debug("Assembling XACML...");

		try {
			forgeEnvironment(environment);
			for (Subject subject : subjects) {
				forgeSubject(subject);
			}
			for (Action action : actions) {
				forgeAction(action);
			}
			for (Resource resource : resources) {
				forgeResource(resource);
			}
		} catch (XacmlSdkException e1) {
			e1.printStackTrace();
			LOGGER.error(e1.getLocalizedMessage());
		}
		xacmlRequest.getAttributes().addAll(subjectCategory);
		xacmlRequest.getAttributes().addAll(resourceCategory);
		xacmlRequest.getAttributes().addAll(actionCategory);
		xacmlRequest.getAttributes().addAll(environmentCategory);
		xacmlRequest.setCombinedDecision(false);
		xacmlRequest.setReturnPolicyIdList(false);

		this.request = xacmlRequest;

		StringWriter stringRequest = new StringWriter();
		if(LOGGER.isDebugEnabled()) {
			
			try {
				JAXBCONTEXT.createMarshaller().marshal(request, stringRequest);
			} catch (JAXBException e1) {
				LOGGER.error(e1.getLocalizedMessage());
			}
			/*AUTHZFORCE-62 -JAXBContext is initialized at each request. This causes major performance issue*/
//			try {
//				JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class).createMarshaller().marshal(request, stringRequest);
//			} catch (JAXBException e) {
//				e.printStackTrace();
//				LOGGER.error(e.getLocalizedMessage());
//			}
			LOGGER.debug("XACML Request created: " + stringRequest.toString());
		}

		return request;
	}
	
	@Override
	public Responses getAuthZ(List<Subject> subject, List<Resource> resources,
			List<Action> actions, Environment environment)
			throws XacmlSdkException {

		long startInGetAuthz = System.currentTimeMillis();
		Responses responses = new Responses();

		long startTimeCreateRequest = System.currentTimeMillis();
		myRequest = createXacmlRequest(subject, resources, actions,environment);
		long endTimeCreateRequest = System.currentTimeMillis();
		
		long startTimeToMarhsallReq = System.currentTimeMillis();
		StringWriter writer = new StringWriter();
		/*AUTHZFORCE-62 -JAXBContext is initialized at each request. This causes major performance issue*/
		//try {
			//JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class).createMarshaller().marshal(myRequest, writer);
		//} catch (Exception e) {
			//e.printStackTrace();
			//LOGGER.error(e.getLocalizedMessage());
		//}
		try {
			JAXBCONTEXT.createMarshaller().marshal(myRequest, writer);
		} catch (JAXBException e1) {
			LOGGER.error(e1.getLocalizedMessage());
			throw new XacmlSdkException(e1);
		}
		long endTimeToMarhsallReq = System.currentTimeMillis();
		

		long startTimeCommPDP = System.currentTimeMillis();
		// FIXME: Fix this time consuming String unmarshalling.
		String myResponseTmp = webResource.type(MediaType.APPLICATION_XML).post(String.class, writer.toString());
		long endTimeCommPDP = System.currentTimeMillis();
		
		long startTimeToMarhsallResp = System.currentTimeMillis();
		Response myResponse = null;
		LOGGER.debug(myResponseTmp);
		try {
			myResponse = (Response) JAXBCONTEXT.createUnmarshaller().unmarshal(new StringReader(myResponseTmp));
		} catch (JAXBException e1) {
			LOGGER.error(e1.getLocalizedMessage());
			throw new XacmlSdkException(e1);
		}
		/*AUTHZFORCE-62 -JAXBContext is initialized at each request. This causes major performance issue*/
		//try {
		//	myResponse = (Response) JAXBContext.newInstance(Response.class).createUnmarshaller().unmarshal(new StringReader(myResponseTmp));
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	LOGGER.error(e.getLocalizedMessage());
		//}		
		long endTimeToMarhsallResp = System.currentTimeMillis();

		long startTimeParseResponse = System.currentTimeMillis();
		// FIXME: possible NPE on each of the getContent
		for (Result result : myResponse.getResults()) {
			com.thalesgroup.authzforce.sdk.core.schema.Response response = new com.thalesgroup.authzforce.sdk.core.schema.Response();
			for (Attributes returnedAttr : result.getAttributes()) {
				for (Attribute attr : returnedAttr.getAttributes()) {
					if (attr.getAttributeId()
							.equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID
									.value())) {
						for (AttributeValueType attrValue : attr.getAttributeValues()) {
							response.setResourceId(String.valueOf(attrValue.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(
							XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
						for (AttributeValueType attrValue : attr
								.getAttributeValues()) {
							response.setAction(String.valueOf(attrValue.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(
							XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
						for (AttributeValueType attrValue : attr
								.getAttributeValues()) {
							response.setSubject(String.valueOf(attrValue.getContent().get(0)));
						}
					}
				}
			}
			response.setDecision(result.getDecision());
			responses.getResponse().add(response);
			
			this.clearRequest();
		}
		long endTimeParseResponse = System.currentTimeMillis();
		long endTimeIngetAuthz = System.currentTimeMillis();
		long timeIngetAuthz = endTimeIngetAuthz - startInGetAuthz;
		long timeCreateREq = endTimeCreateRequest - startTimeCreateRequest;
		long timeToMarshallReq = endTimeToMarhsallReq - startTimeToMarhsallReq;
		long timeCommPDP = endTimeCommPDP - startTimeCommPDP;
		long timeToMarhsallResp = endTimeToMarhsallResp - startTimeToMarhsallResp;
		long timeParseResponse = endTimeParseResponse - startTimeParseResponse;

		loggerPerf.debug("XACML-SDK - getAuthZ - RequestId: "+null+"\n"
				+ "Time in method: "+timeIngetAuthz+" ms \n"
				+ "Time to create xacml req: "+timeCreateREq+" ms \n"
				+ "Time to marshall req: "+timeToMarshallReq+" ms \n"
				+ "Time Comm with PDP: "+timeCommPDP+" ms \n"
				+ "Time to unmarshall resp: "+timeToMarhsallResp+" ms \n"
				+ "Time to parse reponse: "+timeParseResponse+" ms \n");
		return responses;	
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
	@Override
	public com.thalesgroup.authzforce.sdk.core.schema.Response getAuthZ(Subject subject, Resource resource, Action action,
			Environment environment) throws XacmlSdkException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		List<Action> tmpActionList = new ArrayList<Action>();
		tmpResourceList.add(resource);
		tmpActionList.add(action);
		
		return getAuthZ(subject, tmpResourceList, tmpActionList, environment).getResponse().get(0);
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
