package com.thalesgroup.authzforce.sdk.xacml.utils;

import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLDatatypes;
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

	/**
	 * This method is used to parse the response from the PDP
	 * 
	 * @param xmlResponse
	 *            Response from the PDP (String)
	 * @return HashMap
	 */
	// public Map<String, String> parseResponse(String xmlResponse) {
	// org.jdom2.Document doc = null;
	// org.jdom2.Element root = null;
	// HashMap<String, String> obligations = new HashMap<String, String>();
	//
	// LOGGER.info("Parsing XML response");
	// SAXBuilder sxb = new SAXBuilder();
	// Reader in = new StringReader(xmlResponse);
	// try {
	// /*
	// * Building
	// */
	// doc = sxb.build(in);
	// root = doc.getRootElement();
	//
	// } catch (JDOMException e) {
	// LOGGER.error(e.getLocalizedMessage());
	// } catch (IOException e) {
	// LOGGER.error(e.getLocalizedMessage());
	// }
	//
	// /*
	// * Parsing
	// */
	// // get the default names
	// List attrs = root.getChildren();
	// Iterator i = attrs.iterator();
	// while (i.hasNext()) {
	// Element current = (Element) i.next();
	// // try {
	// ArrayList<Element> courant = (ArrayList<Element>) parseChildren(current);
	// for (int ii = 0; ii < courant.size(); ii++) {
	// if (courant.get(ii).getName().equals("StatusCode")) {
	// obligations.put(courant.get(ii).getName(), courant.get(ii)
	// .getAttributeValue("Value"));
	// } else if (courant.get(ii).getName().equals("Decision")) {
	// obligations.put(courant.get(ii).getName(), courant.get(ii)
	// .getText());
	// }
	// }
	// // } catch (ParsingException e) {
	// // LOGGER.error(e.getLocalizedMessage());
	// // }
	// }
	//
	// Iterator y = obligations.entrySet().iterator();
	// while (y.hasNext()) {
	// Map.Entry ent = (Map.Entry) y.next();
	// Object key = ent.getKey();
	// Object value = ent.getValue();
	// LOGGER.debug(key + ": " + value);
	// }
	// LOGGER.info("Parsing OK.");
	// LOGGER.debug("Response: \n" + xmlResponse.replaceAll("\"", "'"));
	//
	// return obligations;
	// }

	/**
	 * This method is used to parse the response from the PDP
	 * 
	 * @param xmlResponse
	 *            Response from the PDP (String)
	 * @return HashMap
	 */
	// public Map<String, String> parseResponse(Document xmlResponse) {
	// Document doc = xmlResponse;
	// Element root = doc.getRootElement();
	// HashMap<String, String> obligations = new HashMap<String, String>();
	//
	// LOGGER.info("Parsing XML response");
	//
	// /*
	// * Parsing
	// */
	// // get the default names
	// List attrs = root.getChildren();
	// Iterator i = attrs.iterator();
	// while (i.hasNext()) {
	// Element current = (Element) i.next();
	// ArrayList<Element> courant = (ArrayList<Element>) parseChildren(current);
	// for (int ii = 0; ii < courant.size(); ii++) {
	// if (courant.get(ii).getName().equals("StatusCode")) {
	// obligations.put(courant.get(ii).getName(), courant.get(ii)
	// .getAttributeValue("Value"));
	// } else if (courant.get(ii).getName().equals("Decision")) {
	// obligations.put(courant.get(ii).getName(), courant.get(ii)
	// .getText());
	// }
	// }
	// }
	//
	// Iterator y = obligations.entrySet().iterator();
	// while (y.hasNext()) {
	// Map.Entry ent = (Map.Entry) y.next();
	// Object key = ent.getKey();
	// Object value = ent.getValue();
	// LOGGER.debug(key + ": " + value);
	// }
	// LOGGER.info("Parsing OK.");
	//
	// return obligations;
	// }

	/**
	 * This method is used to create the XML request based on the RequestType
	 * 
	 * @return XML Request (String)
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

	/**
	 * 
	 * @param subjectAttributes
	 * @param subjectMap
	 * @return
	 * 
	 *         Forging the subject attribute. TODO: Support other standards
	 *         Datatypes provided by Oasis
	 * @throws XacmlSdkException
	 */
	private List<AttributeType> forgeSubject(
			List<AttributeType> subjectAttributes,
			Map<String, String> subjectMap) throws XacmlSdkException {
		if (subjectAttributes != null && subjectMap != null) {
			// subject
			LOGGER.debug("Forging subject...");
			AttributeType subjAttr = new AttributeType();
			AttributeValueType subjAttrVal = new AttributeValueType();
			Iterator itKey = subjectMap.keySet().iterator();
			Iterator itVal = subjectMap.values().iterator();
			while (itKey.hasNext()) {
				subjAttr.setAttributeId(((String) (itKey.next())));
				subjAttr.setDataType(XACMLDatatypes.XACML_DATATYPE_STRING);

				Object itValObj = itVal.next();
				if (itValObj instanceof String) {
					subjAttrVal.getContent().add(((String) (itValObj)));
					subjAttr.getAttributeValue().add(subjAttrVal);

					subjectAttributes.add(subjAttr);
					subjAttr = new AttributeType();
					subjAttrVal = new AttributeValueType();
				} else if (itValObj instanceof List) {
					for (int i = 0; i < ((List<String>) (itValObj)).size(); i++) {
						// TODO: put roles here
						LOGGER.debug(subjAttr.getAttributeId() + ": "
								+ ((List) itValObj).get(i));
						subjAttrVal.getContent().add(((List) itValObj).get(i));
						subjAttr.getAttributeValue().add(subjAttrVal);

						subjectAttributes.add(subjAttr);
						subjAttr = new AttributeType();
						subjAttrVal = new AttributeValueType();
					}
				}
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_SUBJECT);
		}
		return subjectAttributes;
	}

	/**
	 * 
	 * @param resAttributes
	 * @param resourceMap
	 * @return
	 * 
	 *         Forging the resource attribute. TODO: Support other standards
	 *         Datatypes provided by Oasis
	 * @throws XacmlSdkException
	 */
	private List<AttributeType> forgeResource(
			List<AttributeType> resAttributes, Map<String, String> resourceMap)
			throws XacmlSdkException {
		if (resAttributes != null && resourceMap != null) {
			// resource
			LOGGER.debug("Forging resource...");

			AttributeValueType resAttrVal = new AttributeValueType();
			AttributeType resAttr = new AttributeType();
			Iterator itKey = resourceMap.keySet().iterator();
			Iterator itVal = resourceMap.values().iterator();
			while (itKey.hasNext()) {
				resAttr.setAttributeId(((String) (itKey.next())));
				resAttr.setDataType(XACMLDatatypes.XACML_DATATYPE_STRING);

				resAttrVal.getContent().add(((String) (itVal.next())));
				resAttr.getAttributeValue().add(resAttrVal);

				resAttributes.add(resAttr);

				resAttr = new AttributeType();
				resAttrVal = new AttributeValueType();
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_RESOURCE.value());
		}
		return resAttributes;
	}

	/**
	 * 
	 * @param actionAttributes
	 * @param actionMap
	 * @return
	 * 
	 *         Forging the action attribute. TODO: Support other standards
	 *         Datatypes provided by Oasis
	 * @throws XacmlSdkException
	 */
	private List<AttributeType> forgeAction(
			List<AttributeType> actionAttributes, Map<String, String> actionMap)
			throws XacmlSdkException {
		if (actionAttributes != null && actionMap != null) {
			// action
			LOGGER.debug("Forging action...");

			AttributeType actionAttr = new AttributeType();
			AttributeValueType actionAttrVal = new AttributeValueType();
			Iterator itKey = actionMap.keySet().iterator();
			Iterator itVal = actionMap.values().iterator();
			while (itKey.hasNext()) {
				actionAttr.setAttributeId(((String) (itKey.next())));
				actionAttr.setDataType(XACMLDatatypes.XACML_DATATYPE_STRING);
				actionAttrVal.getContent().add(((String) (itVal.next())));
				actionAttr.getAttributeValue().add(actionAttrVal);

				actionAttributes.add(actionAttr);
				actionAttr = new AttributeType();
				actionAttrVal = new AttributeValueType();
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ACTION.value());
		}
		return actionAttributes;
	}

	private List<AttributeType> forgeEnvironment(
			Map<String, String> environmentMap) throws XacmlSdkException {
		List<AttributeType> environmentAttributes = new ArrayList<AttributeType>();
		if (environmentMap != null) {
			LOGGER.debug("Forging environment...");
			AttributeType environmentAttr = new AttributeType();
			AttributeValueType actionAttrVal = new AttributeValueType();
			Iterator itKey = environmentMap.keySet().iterator();
			Iterator itVal = environmentMap.values().iterator();
			while (itKey.hasNext()) {
				environmentAttr.setAttributeId(((String) (itKey.next())));
				environmentAttr
						.setDataType(XACMLDatatypes.XACML_DATATYPE_STRING);
				actionAttrVal.getContent().add(((String) (itVal.next())));
				environmentAttr.getAttributeValue().add(actionAttrVal);

				environmentAttributes.add(environmentAttr);
				environmentAttr = new AttributeType();
				actionAttrVal = new AttributeValueType();
			}
		} else {
			throw new XacmlSdkException(
					XacmlSdkExceptionCodes.MISSING_ENVIRONMENT.value());
		}
		return environmentAttributes;
	}

	/**
	 * 
	 * @param environmentAttribute
	 *            : XACMLAttributeId, environmentVariable (i.e. Time)
	 * @throws XacmlSdkException
	 * 
	 */
	private Request addEnvironmentVariable(
			Map<String, String> environmentAttribute) throws XacmlSdkException {
		ArrayList<AttributeType> envAttributes = new ArrayList<AttributeType>();
		EnvironmentType xacmlEnvironment = new EnvironmentType();

		forgeEnvironment(environmentAttribute);

		xacmlEnvironment.getAttribute().addAll(envAttributes);
		if (request != null) {
			request.setEnvironment(xacmlEnvironment);
		}

		return request;
	}

	/**
	 * @throws XacmlSdkException
	 * @notImplemented
	 * 
	 *                 Multiple Resource Profile
	 */
	private Request createXacmlRequest(Map<String, String> subjectMap,
			List<Map<String, String>> resourceMap,
			Map<String, String> actionMap, Map<String, String> environmentMap)
			throws XacmlSdkException {

		Request xacmlRequest = new Request();

		ArrayList<AttributeType> subjectAttributes = new ArrayList<AttributeType>();
		ArrayList<AttributeType> resAttributes;
		ArrayList<AttributeType> actionAttributes = new ArrayList<AttributeType>();
		ArrayList<AttributeType> envAttributes = new ArrayList<AttributeType>();

		EnvironmentType xacmlEnvironment = new EnvironmentType();
		ActionType xacmlAction = new ActionType();
		SubjectType xacmlSubject = new SubjectType();
		ResourceType xacmlResource;

		List<ResourceType> xacmlResourceList = new ArrayList<ResourceType>();

		for (Map<String, String> resourceList : resourceMap) {
			xacmlResource = new ResourceType();
			resAttributes = new ArrayList<AttributeType>();
			resAttributes = (ArrayList<AttributeType>) forgeResource(
					resAttributes, resourceList);
			xacmlResource.getAttribute().addAll(resAttributes);
			xacmlResourceList.add(xacmlResource);
		}

		subjectAttributes = (ArrayList<AttributeType>) forgeSubject(
				subjectAttributes, subjectMap);
		actionAttributes = (ArrayList<AttributeType>) forgeAction(
				actionAttributes, actionMap);
		envAttributes = (ArrayList<AttributeType>) forgeEnvironment(environmentMap);

		xacmlSubject.getAttribute().addAll(subjectAttributes);

		xacmlAction.getAttribute().addAll(actionAttributes);

		xacmlEnvironment.getAttribute().addAll(envAttributes);

		LOGGER.debug("Assembling XACML...");
		xacmlRequest.getSubject().add(xacmlSubject);
		xacmlRequest.getResource().addAll(xacmlResourceList);
		xacmlRequest.setAction(xacmlAction);
		xacmlRequest.setEnvironment(xacmlEnvironment);

		this.request = xacmlRequest;

		return xacmlRequest;
	}

	public Response getAuthZ(Map<String, String> mySubject,
			Map<String, String> myResource, Map<String, String> myAction,
			Map<String, String> myEnvironment) throws XacmlSdkException {

		List<Map<String, String>> resourceList = new ArrayList<Map<String, String>>();
		resourceList.add(myResource);

		return getAuthZ(mySubject, resourceList, myAction, myEnvironment);
	}

	public Response getAuthZ(Map<String, String> mySubject,
			List<Map<String, String>> resourceList,
			Map<String, String> myAction, Map<String, String> myEnvironment)
			throws XacmlSdkException {
		com.thalesgroup.authzforce.sdk.core.schema.Response response = new com.thalesgroup.authzforce.sdk.core.schema.Response();
		/*
		 * FIXME: Hack for xacml v2.0
		 * 
		 * When we go to xacml v3.0 or v2.0 Multi Resource Profile no need to use a temp map and a loop. 
		 * Just do:
		 * createXacmlRequest(mySubject, resourceList, myAction, myEnvironment);
		 * ResponseType myResponse = webResource.accept(MediaType.APPLICATION_XML)
		 *			.type(MediaType.APPLICATION_XML)
		 *			.post(ResponseType.class, this.toString());
		 * for (ResultType result : myResponse.getResult()) {
		 *		response.setResponse(result.getResourceId(), result.getDecision()
		 *				.value());
		 *	}
		 */
		List<Map<String, String>> tmpResourceList = new ArrayList<Map<String, String>>();
		for (Map<String, String> map : resourceList) {
			tmpResourceList.add(map);
			createXacmlRequest(mySubject, tmpResourceList, myAction, myEnvironment);
			ResponseType myResponse = webResource.accept(MediaType.APPLICATION_XML)
					.type(MediaType.APPLICATION_XML)
					.post(ResponseType.class, this.toString());
			for (ResultType result : myResponse.getResult()) {
				response.setResponse(result.getResourceId(), result.getDecision()
						.value());
			}
			this.clearRequest();
			tmpResourceList.clear();
		}

		return response;
	}

}