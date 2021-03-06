package org.ow2.authzforce.sdk.pdp;

import java.net.URI;
import java.util.Arrays;

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
import org.ow2.authzforce.sdk.utils.PapService;
import org.ow2.authzforce.sdk.utils.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleDecisionProfileRequest {
	
private static final Logger LOGGER = LoggerFactory.getLogger(MultipleDecisionProfileRequest.class);
	
	private static final String SUBJECT = "ThalesId";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String RESOURCE_2 = "http://www.google.fr";
	private static final String ACTION = "HEAD";
	
	
	public static void main(String[] args) throws XacmlSdkException {
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

		URI PDP_ENDPOINT = ServerSetup.getRootURL(ServerSetup.getServer());
		String DOMAIN_ID = PapService.setupBasicDomain(PDP_ENDPOINT,"MultipleDecisionProfileRequest");
		XacmlSdkImpl myXacml = new XacmlSdkImpl(PDP_ENDPOINT,DOMAIN_ID, null);
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
