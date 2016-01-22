package com.thalesgroup.authzforce.sdk.main;

import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.impl.XacmlSdkImpl;

public class MultipleDecisionProfileRequest {
	
private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDecisionProfileRequest.class);
	
	private static final String PDP_ENDPOINT = "http://127.0.0.1:8080/authzforce-rest-service-4.1.3-OCW";
	private static final String DOMAIN_ID = "5e022256-6d0f-4eb8-aa9d-77db3d4ad141";

	private static final String SUBJECT = "ThalesId";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String RESOURCE_2 = "http://www.google.fr";
	private static final String ACTION = "HEAD";
	
	
	public static void main(String[] args) {
		SubjectCategory subjectCat = new SubjectCategory();
		ResourceCategory resourceCat = new ResourceCategory();
		ResourceCategory resourceCat2 = new ResourceCategory();
		ActionCategory actionCategory = new ActionCategory();
		EnvironmentCategory environmentCategory = new EnvironmentCategory();
		
		subjectCat.addAttribute(new Subject(SUBJECT));
		resourceCat.addAttribute(new Resource(RESOURCE));
		resourceCat2.addAttribute(new Resource(RESOURCE_2));
		actionCategory.addAttribute(new Action(ACTION));
		environmentCategory.addAttribute(new Environment("TEST_MultipleDecisionProfileRequest"));
		
		XacmlSdkImpl myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT), DOMAIN_ID, null);
		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subjectCat, Arrays.asList(resourceCat, resourceCat2), actionCategory, environmentCategory);
		} catch (XacmlSdkException e) {
			LOGGER.error(e.getLocalizedMessage());
			LOGGER.error(e.getCause().getLocalizedMessage());
		}
		if(responses != null) {
			for (Response response : responses.getResponses()) {
				LOGGER.info(response.getActionId() + " on "
						+ response.getResourceId() + ": " 
						+ response.getDecision().value() + " for " 
						+ response.getSubjectId());
			}
		}
	}

}
