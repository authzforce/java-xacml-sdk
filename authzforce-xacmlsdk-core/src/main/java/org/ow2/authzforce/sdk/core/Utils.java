package com.thalesgroup.authzforce.sdk.core;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.ow2.authzforce.xacml.identifiers.XACMLAttributeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.Category;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.core.utils.ResponsesFactory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkExceptionCodes;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Result;

public final class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

//	private static List<Attributes> resourceCategory = new LinkedList<Attributes>();
//	private static List<Attributes> actionCategory = new LinkedList<Attributes>();
//	private static List<Attributes> subjectCategory = new LinkedList<Attributes>();
//	private static List<Attributes> environmentCategory = new LinkedList<Attributes>();

	/**
	 * 
	 * @param subjects
	 * @param resources
	 * @param actions
	 * @param environment
	 * @return
	 * 
	 * @return
	 * @throws XacmlSdkException
	 */
	public static Request createXacmlRequest(List<SubjectCategory> subjects, List<ResourceCategory> resources,
			List<ActionCategory> actions, List<EnvironmentCategory> environments) throws XacmlSdkException {

		if (null == subjects || null == resources || null == actions || null == environments) {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.CATEGORY_IS_NULL);
		}
		if (subjects.size() > 1 || resources.size() > 1 || actions.size() > 1 || environments.size() > 1) {
			LOGGER.warn("Multiple categories: Be sure that your PDP support multiple decision profile");
		}

		LOGGER.debug("Assembling Request...");
		try {

			for (SubjectCategory subject : subjects) {
				Utils.check(subject);
			}
			for (ResourceCategory resource : resources) {
				Utils.check(resource);
			}
			for (ActionCategory action : actions) {
				Utils.check(action);
			}
			for (EnvironmentCategory environment : environments) {
				Utils.check(environment);
			}
		} catch (XacmlSdkException e) {
			throw new XacmlSdkException(e);
		}

//		environmentCategory.addAll(environments);
//		subjectCategory.addAll(subjects);
//		actionCategory.addAll(actions);
//		resourceCategory.addAll(resources);
		List<Attributes> attributes = new ArrayList<Attributes>();
		attributes.addAll(environments);
		attributes.addAll(subjects);
		attributes.addAll(actions);
		attributes.addAll(resources);
		boolean combinedDecision = false;
		boolean returnPolicyIdList = false;
		
		final Request xacmlRequest = new Request(null, attributes, null, returnPolicyIdList, combinedDecision);

		if (LOGGER.isDebugEnabled()) {
			StringWriter stringRequest = new StringWriter();
			try {
				Marshaller marshaller = JAXBContext
						.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class).createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
				marshaller.marshal(xacmlRequest, stringRequest);
			} catch (JAXBException e) {
				e.printStackTrace();
				LOGGER.error(e.getLocalizedMessage());
			}
			LOGGER.debug("XACML Request created: {}", stringRequest.toString());
		}

		return xacmlRequest;
	}

	private static void check(Category category) throws XacmlSdkException {
		if (null == category) {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.CATEGORY_IS_NULL);
		}
	}

	/**
	 * This method take a XACML Response and convert it to a Responses object
	 * easy to use in the SDK
	 * 
	 * @param myResponse
	 * @return
	 * @throws XacmlSdkException 
	 */
	public static ResponsesFactory extractResponse(Response myResponse) throws XacmlSdkException {

		com.thalesgroup.authzforce.sdk.core.schema.Responses responses = new com.thalesgroup.authzforce.sdk.core.schema.Responses();

		for (Result result : myResponse.getResults()) {
			com.thalesgroup.authzforce.sdk.core.schema.Response response = new com.thalesgroup.authzforce.sdk.core.schema.Response();
			if(result.getDecision().equals(DecisionType.INDETERMINATE) || result.getDecision().equals(DecisionType.NOT_APPLICABLE)) {
				throw new XacmlSdkException("Decision is " + result.getDecision().value());
			}
			response.setDecision(result.getDecision());
			for (Attributes attrs : result.getAttributes()) {
				for (Attribute attr : attrs.getAttributes()) {
					if (attr.getAttributeId().equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
						if (null != attr.getAttributeValues() && attr.getAttributeValues().size() > 0) {
							response.setSubjectId(String.valueOf(attr.getAttributeValues().get(0).getContent()));
						}
					} else if (attr.getAttributeId().equals(XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
						if (null != attr.getAttributeValues() && attr.getAttributeValues().size() > 0) {
							response.setActionId(String.valueOf(attr.getAttributeValues().get(0).getContent()));

						}
					} else if (attr.getAttributeId().equals(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
						if (null != attr.getAttributeValues() && attr.getAttributeValues().size() > 0) {
							response.setResourceId(String.valueOf(attr.getAttributeValues().get(0).getContent()));
						}
					}
				}
			}

			responses.getResponses().add(response);
		}

		return new ResponsesFactory(responses);
	}

	public static void logRawResponse(Response myResponse) {
		StringWriter stringRequest = new StringWriter();
		try {
			JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Response.class).createMarshaller()
					.marshal(myResponse, stringRequest);
		} catch (JAXBException e) {
			e.printStackTrace();
			LOGGER.error(e.getLocalizedMessage());
		}
		LOGGER.debug("XACML Response\n[ {} ]", stringRequest.toString());

	}
}