package com.thalesgroup.authzforce.sdk.main;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.core.schema.XACMLAttributeId;
import com.thalesgroup.authzforce.sdk.core.schema.category.ActionCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.EnvironmentCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.ResourceCategory;
import com.thalesgroup.authzforce.sdk.core.schema.category.SubjectCategory;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;
import com.thalesgroup.authzforce.sdk.impl.XacmlSdkImpl;

public class MultiValuedAttributeRequest {

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

		Subject nameAttr = new Subject(new SimpleDateFormat("YYY-MM-DD").format(System.currentTimeMillis()));
		nameAttr.setAttributeId(XACMLAttributeId.XACML_SUBJECT_REQUEST_TIME.value());

		subjectCat.addAttribute(new Subject(SUBJECT));
		subjectCat.addAttribute(nameAttr);
		resourceCat.addAttribute(new Resource(RESOURCE));
		actionCategory.addAttribute(new Action(ACTION));
		environmentCategory.addAttribute(new Environment("TEST_MultiValuedAttributed"));

		XacmlSdkImpl myXacml = new XacmlSdkImpl(URI.create(PDP_ENDPOINT), DOMAIN_ID, null);
		Responses responses = null;
		try {
			responses = myXacml.getAuthZ(subjectCat, resourceCat, actionCategory, environmentCategory);
		} catch (XacmlSdkException e) {
			LOGGER.error(e.getLocalizedMessage());
			LOGGER.error(e.getCause().getLocalizedMessage());
		}
		if (responses != null) {
			for (Response response : responses.getResponse()) {
				LOGGER.info(response.getAction() + " on " + response.getResourceId() + ": "
						+ response.getDecision().value() + " for " + response.getSubject());
			}
		}
	}

}
