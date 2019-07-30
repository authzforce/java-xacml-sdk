package org.ow2.authzforce.sdk.pdp;

import java.net.URI;
import java.text.SimpleDateFormat;

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
import org.ow2.authzforce.xacml.identifiers.XACMLAttributeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiValuedAttributeRequest {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleAuthorizationRequest.class);


	private static final String SUBJECT = "ThalesId";
	private static final String RESOURCE = "http://www.opencloudware.org";
	private static final String ACTION = "HEAD";

	public static void main(String[] args) throws XacmlSdkException {
		SubjectCategory subjectCat = new SubjectCategory();
		ResourceCategory resourceCat = new ResourceCategory();
		ActionCategory actionCategory = new ActionCategory();
		EnvironmentCategory environmentCategory = new EnvironmentCategory();

		Subject nameAttr = new Subject(new SimpleDateFormat("YYY-MM-DD").format(System.currentTimeMillis()));
		nameAttr.setAttributeId(XACMLAttributeId.XACML_SUBJECT_REQUEST_TIME.value());

		subjectCat.addAttribute(new Subject(SUBJECT));
		subjectCat.addAttribute(nameAttr);
		resourceCat.addAttribute(new Resource(RESOURCE));
		actionCategory.addAttribute(new Action(ACTION));
		environmentCategory.addAttribute(new Environment("TEST_MultiValuedAttributed"));

		URI PDP_ENDPOINT = ServerSetup.getRootURL(ServerSetup.getServer());
		String DOMAIN_ID = PapService.setupBasicDomain(PDP_ENDPOINT,"MultiValuedAttributeRequest");
		XacmlSdkImpl myXacml = new XacmlSdkImpl(PDP_ENDPOINT, DOMAIN_ID, null);
		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subjectCat, resourceCat, actionCategory, environmentCategory);
		} catch (XacmlSdkException e) {
			LOGGER.error(e.getLocalizedMessage());
			LOGGER.error(e.getCause().getLocalizedMessage());
		}
		if (responses != null) {
			for (Response response : responses.getResponses()) {
				LOGGER.info(response.getActionId() + " on " + response.getResourceId() + ": "
						+ response.getDecision().value() + " for " + response.getSubjectId());
			}
		}
	}

}
