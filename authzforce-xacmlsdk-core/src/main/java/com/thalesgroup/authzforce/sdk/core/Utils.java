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

	private static void forgeResource(Resource resource) throws XacmlSdkException {
		if (resource != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Resource...");
			if (resourceCategory.size() > 0) {
				if (resource.getAttributeId().equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : resourceCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
							if (attrType.getAttributeId().equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
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
							attr.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE.value());
							attr.getAttributes().add(resource);
							resourceCategory.add(attr);
						}
					} else {
						resourceCategory.get(resourceCategory.size() - 1).getAttributes().add(resource);
					}
				} else {
					resourceCategory.get(resourceCategory.size() - 1).getAttributes().add(resource);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_RESOURCE_CATEGORY_RESOURCE.value());
				attr.getAttributes().add(resource);
				resourceCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.MISSING_RESOURCE.value());
		}
	}

	private static void forgeSubject(Subject subject) throws XacmlSdkException {
		if (subject != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Subject...");
			if (subjectCategory.size() > 0) {
				if (subject.getAttributeId().equals(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : subjectCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
							if (attrType.getAttributeId().equals(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
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
							attr.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT.value());
							attr.getAttributes().add(subject);
							subjectCategory.add(attr);
						}
					} else {
						subjectCategory.get(subjectCategory.size() - 1).getAttributes().add(subject);
					}
				} else {
					subjectCategory.get(subjectCategory.size() - 1).getAttributes().add(subject);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_1_0_SUBJECT_CATEGORY_SUBJECT.value());
				attr.getAttributes().add(subject);
				subjectCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.MISSING_SUBJECT.value());
		}
	}

	private static void forgeAction(Action action) throws XacmlSdkException {
		if (action != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Action...");
			if (actionCategory.size() > 0) {
				if (action.getAttributeId().equals(XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : actionCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
							if (attrType.getAttributeId().equals(XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
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
							attr.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION.value());
							attr.getAttributes().add(action);
							actionCategory.add(attr);
						}
					} else {
						actionCategory.get(actionCategory.size() - 1).getAttributes().add(action);
					}
				} else {
					actionCategory.get(actionCategory.size() - 1).getAttributes().add(action);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ACTION_CATEGORY_ACTION.value());
				attr.getAttributes().add(action);
				actionCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.MISSING_ACTION.value());
		}
	}

	private static void forgeEnvironment(Environment environment) throws XacmlSdkException {
		if (environment != null) {
			Attributes attr = new Attributes();
			Attribute attrId = new Attribute();
			LOGGER.debug("Forging Environment...");
			if (environmentCategory.size() > 0) {
				if (environment.getAttributeId()
						.equals(XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID.value())) {
					boolean containId = false;
					for (Attributes attrsType : environmentCategory) {
						for (Attribute attrType : attrsType.getAttributes()) {
							if (attrType.getAttributeId()
									.equals(XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID.value())) {
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
							attr.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT.value());
							attr.getAttributes().add(environment);
							environmentCategory.add(attr);
						}
					} else {
						environmentCategory.get(environmentCategory.size() - 1).getAttributes().add(environment);
					}
				} else {
					environmentCategory.get(environmentCategory.size() - 1).getAttributes().add(environment);
				}
			} else {
				attr.setCategory(XACMLAttributeId.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT.value());
				attr.getAttributes().add(environment);
				environmentCategory.add(attr);
			}
		} else {
			throw new XacmlSdkException(XacmlSdkExceptionCodes.MISSING_ENVIRONMENT.value());
		}
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