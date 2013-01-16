package com.thalesgroup.authzforce.sdk.xacml.utils;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import oasis.names.tc.xacml._2_0.context.schema.os.ActionType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeValueType;
import oasis.names.tc.xacml._2_0.context.schema.os.EnvironmentType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResultType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;

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

	private Request request;
	private WebResource webResource;
	private Client client;

	/**
	 * Constructor
	 * 
	 * @param serverEndpoint
	 */
	public XacmlSdkImpl(URI serverEndpoint) {
		this.client = Client.create();
		this.webResource = this.client.resource(serverEndpoint);
	}

	private void clearRequest() {
		this.request = new Request();
	}

	/* (non-Javadoc)
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#toString()
	 */
	@Override
	public String toString() {
		LOGGER.debug("Create XML (marshalling)");
		java.io.StringWriter sw = new StringWriter();
		try {
			Marshaller marsh = JAXBContext.newInstance(Request.class)
					.createMarshaller();
			marsh.marshal(new JAXBElement<Request>(
					new QName("urn:oasis:names:tc:xacml:2.0:context:schema:os",
							"Request"), Request.class, request), sw);
		} catch (JAXBException e) {
			LOGGER.error("", e);
		}
		LOGGER.debug(new String(sw.getBuffer()).replaceAll("\"", "'"));

		return sw.toString();
	}

	private List<AttributeType> forgeResource(Resource resource)
			throws XacmlSdkException {
		List<AttributeType> resAttributes = new ArrayList<AttributeType>();
		AttributeValueType resAttrVal = new AttributeValueType();
		AttributeType resAttr = new AttributeType();

		if (resource != null) {
			LOGGER.debug("Forging resource...");

			resAttr.setAttributeId(resource.getId().value());
			resAttr.setDataType(resource.getDatatype().value());

			resAttrVal.getContent().add(resource.getValue());
			resAttr.getAttributeValue().add(resAttrVal);

			resAttributes.add(resAttr);

			resAttr = new AttributeType();
			resAttrVal = new AttributeValueType();
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_RESOURCE.value());
		}
		return resAttributes;
	}

	private List<AttributeType> forgeSubject(Subject subject)
			throws XacmlSdkException {
		List<AttributeType> subjectAttributes = new ArrayList<AttributeType>();
		AttributeValueType subjectAttrVal = new AttributeValueType();
		AttributeType subjectAttr = new AttributeType();

		if (subject != null) {
			LOGGER.debug("Forging Subject...");

			subjectAttr.setAttributeId(subject.getId().value());
			subjectAttr.setDataType(subject.getDatatype().value());

			subjectAttrVal.getContent().add(subject.getValue());
			subjectAttr.getAttributeValue().add(subjectAttrVal);

			subjectAttributes.add(subjectAttr);

			subjectAttr = new AttributeType();
			subjectAttrVal = new AttributeValueType();
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_SUBJECT.value());
		}
		return subjectAttributes;
	}

	private List<AttributeType> forgeAction(Action action)
			throws XacmlSdkException {
		List<AttributeType> actionAttributes = new ArrayList<AttributeType>();
		AttributeValueType actionAttrVal = new AttributeValueType();
		AttributeType actionAttr = new AttributeType();

		if (action != null) {
			LOGGER.debug("Forging Action...");

			actionAttr.setAttributeId(action.getId().value());
			actionAttr.setDataType(action.getDatatype().value());

			actionAttrVal.getContent().add(action.getValue());
			actionAttr.getAttributeValue().add(actionAttrVal);

			actionAttributes.add(actionAttr);

			actionAttr = new AttributeType();
			actionAttrVal = new AttributeValueType();
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ACTION.value());
		}
		return actionAttributes;
	}

	private List<AttributeType> forgeEnvironment(Environment environment)
			throws XacmlSdkException {
		List<AttributeType> environmentAttributes = new ArrayList<AttributeType>();
		AttributeValueType environmentAttrVal = new AttributeValueType();
		AttributeType environmentAttr = new AttributeType();

		if (environment != null) {
			LOGGER.debug("Forging Environment...");

			environmentAttr.setAttributeId(environment.getId().value());
			environmentAttr.setDataType(environment.getDatatype().value());

			environmentAttrVal.getContent().add(environment.getValue());
			environmentAttr.getAttributeValue().add(environmentAttrVal);

			environmentAttributes.add(environmentAttr);

			environmentAttr = new AttributeType();
			environmentAttrVal = new AttributeValueType();
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ENVIRONMENT.value());
		}
		return environmentAttributes;
	}

	private void createXacmlRequest(Subject subject, List<Resource> resources,
			List<Action> actions, Environment environment) {
		Request xacmlRequest = new Request();

		List<AttributeType> subjectAttributes = new ArrayList<AttributeType>();
		List<AttributeType> resAttributes = new ArrayList<AttributeType>();
		List<AttributeType> actionAttributes = new ArrayList<AttributeType>();
		List<AttributeType> envAttributes = new ArrayList<AttributeType>();

		EnvironmentType xacmlEnvironment = new EnvironmentType();
		ActionType xacmlAction = new ActionType();
		SubjectType xacmlSubject = new SubjectType();
		ResourceType xacmlResource;

		List<ResourceType> xacmlResourceList = new ArrayList<ResourceType>();
		try {
			for (Resource resource : resources) {
				xacmlResource = new ResourceType();

				resAttributes = forgeResource(resource);

				xacmlResource.getAttribute().addAll(resAttributes);
				xacmlResourceList.add(xacmlResource);
			}
			for (Action action : actions) {
				xacmlAction = new ActionType();
				actionAttributes = forgeAction(action);
			}

			subjectAttributes = forgeSubject(subject);
			envAttributes = forgeEnvironment(environment);
		} catch (XacmlSdkException e) {
			e.printStackTrace();
		}
		xacmlSubject.getAttribute().addAll(subjectAttributes);

		xacmlAction.getAttribute().addAll(actionAttributes);

		xacmlEnvironment.getAttribute().addAll(envAttributes);

		LOGGER.debug("Assembling XACML...");
		xacmlRequest.getSubject().add(xacmlSubject);
		xacmlRequest.getResource().addAll(xacmlResourceList);
		xacmlRequest.setAction(xacmlAction);
		xacmlRequest.setEnvironment(xacmlEnvironment);

		this.request = xacmlRequest;
	}


	/* (non-Javadoc)
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup.authzforce.sdk.core.schema.Subject, java.util.List, java.util.List, com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	@Override
	public Responses getAuthZ(Subject subject, List<Resource> resources,
			List<Action> actions, Environment environment)
			throws XacmlSdkException {
		Responses responses = new Responses();
		responses.setSubject(subject.getValue());
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		List<Action> tmpActionList = new ArrayList<Action>();
		/*
		 * FIXME: Loop to handle some kind of xacml v3.0 emulation
		 */
		for (Resource resource : resources) {
			tmpResourceList.add(resource);
			for (Action myAction : actions) {
				tmpActionList.add(myAction);
				createXacmlRequest(subject, tmpResourceList, tmpActionList, environment);
				ResponseType myResponse = webResource
						.accept(MediaType.APPLICATION_XML)
						.type(MediaType.APPLICATION_XML)
						.post(ResponseType.class, this.toString());
				for (ResultType result : myResponse.getResult()) {
					Response response = new Response();
					response.setResourceId(result.getResourceId());
					response.setDecision(result.getDecision());
					response.setAction(myAction.getValue());
					responses.getResponse().add(response);
				}
				tmpActionList.clear();
				this.clearRequest();
			}
			tmpResourceList.clear();
		}
		return responses;
	}

	/* (non-Javadoc)
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup.authzforce.sdk.core.schema.Subject, com.thalesgroup.authzforce.sdk.core.schema.Resource, com.thalesgroup.authzforce.sdk.core.schema.Action, com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	@Override
	public Response getAuthZ(Subject subject, Resource resource, Action action,
			Environment environment) throws XacmlSdkException {
		List<Resource> tmpResourceList = new ArrayList<Resource>();
		List<Action> tmpActionList = new ArrayList<Action>();
		tmpResourceList.add(resource);
		tmpActionList.add(action);
		return getAuthZ(subject, tmpResourceList, tmpActionList, environment).getResponse().get(0);
	}

	/* (non-Javadoc)
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup.authzforce.sdk.core.schema.Subject, java.util.List, com.thalesgroup.authzforce.sdk.core.schema.Action, com.thalesgroup.authzforce.sdk.core.schema.Environment)
	 */
	@Override
	public Responses getAuthZ(Subject subject, List<Resource> resource,
			Action action, Environment environment) throws XacmlSdkException {
		List<Action> tmpActionList = new ArrayList<Action>();
		tmpActionList.add(action);
		return getAuthZ(subject, resource, tmpActionList, environment);
	}

	/* (non-Javadoc)
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#getAuthZ(com.thalesgroup.authzforce.sdk.core.schema.Subject, com.thalesgroup.authzforce.sdk.core.schema.Resource, java.util.List, com.thalesgroup.authzforce.sdk.core.schema.Environment)
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