package org.ow2.authzforce.sdk.main;

import java.net.URI;

import org.ow2.authzforce.sdk.core.schema.Action;
import org.ow2.authzforce.sdk.core.schema.Environment;
import org.ow2.authzforce.sdk.core.schema.Resource;
import org.ow2.authzforce.sdk.core.schema.Response;
import org.ow2.authzforce.sdk.core.schema.Responses;
import org.ow2.authzforce.sdk.core.schema.Subject;
import org.ow2.authzforce.sdk.core.schema.category.ActionCategory;
import org.ow2.authzforce.sdk.core.schema.category.EnvironmentCategory;
import org.ow2.authzforce.sdk.core.schema.category.ResourceCategory;
import org.ow2.authzforce.sdk.core.schema.category.SubjectCategory;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.ow2.authzforce.sdk.impl.XacmlSdkImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAuthorizationRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAuthorizationRequest.class);

	private static final String PDP_ENDPOINT = "http://127.0.0.1:8080/authzforce-rest-service-4.1.3-OCW";
	private static final String DOMAIN_ID = "5e022256-6d0f-4eb8-aa9d-77db3d4ad141";

	private static final String SUBJECT = "ThalesId";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String ACTION = "HEAD";

	public static void main(String[] args) {
		SubjectCategory subjectCat = new SubjectCategory();
		ResourceCategory resourceCat = new ResourceCategory();
		ActionCategory actionCategory = new ActionCategory();
		EnvironmentCategory environmentCategory = new EnvironmentCategory();

		subjectCat.addAttribute(new Subject(SUBJECT));
		resourceCat.addAttribute(new Resource(RESOURCE));
		actionCategory.addAttribute(new Action(ACTION));
		environmentCategory.addAttribute(new Environment("TEST_SimpleAuthorizationRequest"));

		XacmlSdkImpl myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT), DOMAIN_ID, null);
		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subjectCat, resourceCat, actionCategory, environmentCategory);
		} catch (XacmlSdkException e) {
			LOGGER.error(e.getLocalizedMessage());
			LOGGER.error(e.getCause().getLocalizedMessage());
		}
		if (null != responses) {
			for (Response response : responses.getResponses()) {
				LOGGER.info(response.getActionId() + " on " + response.getResourceId() + ": "
						+ response.getDecision().value() + " for " + response.getSubjectId());
			}
		}
	}

}
