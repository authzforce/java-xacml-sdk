package com.thalesgroup.authzforce.sdk.core;

import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Request;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.Category;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkExceptionCodes;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Result;

public final class Utils {

	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	private static List<Attributes> resourceCategory = new LinkedList<Attributes>();
	private static List<Attributes> actionCategory = new LinkedList<Attributes>();
	private static List<Attributes> subjectCategory = new LinkedList<Attributes>();
	private static List<Attributes> environmentCategory = new LinkedList<Attributes>();

	/**
	 * 
	 * @param subjects
	 * @param resources
	 * @param actions
	 * @param environment
	 * @return
	 * 
	 * @return
	 */
	public static Request createXacmlRequest(List<SubjectCategory> subjects, List<ResourceCategory> resources,
			List<ActionCategory> actions, List<EnvironmentCategory> environments) {
		Request xacmlRequest = new Request();

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

		}

		environmentCategory.addAll(environments);
		subjectCategory.addAll(subjects);
		actionCategory.addAll(actions);
		resourceCategory.addAll(resources);
		xacmlRequest.getAttributes().addAll(subjectCategory);
		xacmlRequest.getAttributes().addAll(resourceCategory);
		xacmlRequest.getAttributes().addAll(actionCategory);
		xacmlRequest.getAttributes().addAll(environmentCategory);

		xacmlRequest.setCombinedDecision(false);
		xacmlRequest.setReturnPolicyIdList(false);

		if (LOGGER.isDebugEnabled()) {
			StringWriter stringRequest = new StringWriter();
			try {
				JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class).createMarshaller()
						.marshal(xacmlRequest, stringRequest);
			} catch (JAXBException e) {
				e.printStackTrace();
				LOGGER.error(e.getLocalizedMessage());
			}
			LOGGER.debug("XACML Request created: {}", stringRequest.toString());
		}

		return xacmlRequest;
	}

	private static void check(Category category) throws XacmlSdkException {

	}

	public static Responses extractResponse(Response myResponse) {

		Responses responses = new Responses();
		// FIXME: possible NPE on each of the getContent
		for (Result result : myResponse.getResults()) {
			com.thalesgroup.authzforce.sdk.core.schema.Response response = new com.thalesgroup.authzforce.sdk.core.schema.Response();
			for (Attributes returnedAttr : result.getAttributes()) {
				for (Attribute attr : returnedAttr.getAttributes()) {
					if (attr.getAttributeId().equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
						for (AttributeValueType attrValue : attr.getAttributeValues()) {
							response.setResourceId(String.valueOf(attrValue.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
						for (AttributeValueType attrValue : attr.getAttributeValues()) {
							response.setAction(String.valueOf(attrValue.getContent().get(0)));
						}
					} else if (attr.getAttributeId().equals(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
						for (AttributeValueType attrValue : attr.getAttributeValues()) {
							response.setSubject(String.valueOf(attrValue.getContent().get(0)));
						}
					}
				}
			}
			response.setDecision(result.getDecision());
			responses.getResponse().add(response);
		}
		return responses;
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