package com.thalesgroup.pdp.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;

import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.sun.jersey.spi.resource.Singleton;
import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.PDP;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.thalesgroup.authzforce.audit.api.AttributesResolved;
import com.thalesgroup.authzforce.audit.api.AuditLogs;
import com.thalesgroup.authzforce.audit.api.Request;
import com.thalesgroup.pdp.AuthZForce;

/**
 * 
 * @author romain.ferrari[AT]thalesgroup.com
 * 
 *         Interface implementation of AuthZForce PDP
 * 
 */
@Path("/service")
@Singleton
public class AuthZForceImpl implements AuthZForce {

	@Context
	private HttpHeaders myHttpHeaders;

	private static String xmlns = "urn:oasis:names:tc:xacml:2.0:context:schema:os";
	private static PDP myPdp;
	private static ConfigurationStore myStore = null;
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(AuthZForceImpl.class);
	private static final org.apache.log4j.Logger AUDIT_LOGGER = org.apache.log4j.Logger
			.getLogger("audit");

	private String pdpID;
	private static final String PEP_ID = "PEP-RIA";
	// TODO Use configuration file and authentication to retrieve PDP and PEP
	// name

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

	private String propertieFile = "authzforce.properties";

	public AuthZForceImpl(String propertieFilePath) {
		setPropertieFile(propertieFilePath);
		try {
			LOGGER.debug("Loading PDP.....");
			loadPdp();
		} catch (FileNotFoundException e) {
			LOGGER.fatal(e.getLocalizedMessage());
		}
	}
	
	/**
	 * Interface's constructor with configuration initialization and everything.
	 * 
	 * @throws FileNotFoundException
	 */

	public AuthZForceImpl() {
		try {
			LOGGER.debug("Loading PDP.....");
			loadPdp();
		} catch (FileNotFoundException e) {
			LOGGER.fatal(e.getLocalizedMessage());
		}
	}
	
	private void loadPdp() throws FileNotFoundException {
		Properties properties = new Properties();
		try {			
			properties.load(AuthZForceImpl.class.getClassLoader()
					.getResourceAsStream(propertieFile));
//			properties.load(AuthZForceImpl.class.getClassLoader()
//					.getSystemResourceAsStream(propertieFile));
		} catch (IOException e) {
			LOGGER.fatal("An exception occurred : " + e);
		}		
		PropertyConfigurator.configure(properties.getProperty("logProperties"));
		File configFile = new File(properties.getProperty("configFile"));
		LOGGER.info("PDP Configuration Initialization");
		LOGGER.debug("Configuration file: "+configFile.getAbsolutePath());

		try {
			myStore = new ConfigurationStore(configFile);
		} catch (ParsingException e) {
			LOGGER.fatal("Error in Configuration Initialization, stacktrace:");
			LOGGER.fatal(e.getMessage());
		}
		LOGGER.info("PDP Initialization");
		try {
			myPdp = new PDP(myStore.getDefaultPDPConfig());
		} catch (UnknownIdentifierException e) {
			LOGGER.fatal("An exception occurred(UnknownIdentifierException) : ");
			LOGGER.fatal(e.getMessage());
		}
	}
	
	public void setPropertieFile(String propertieFilePath) {
		propertieFile = propertieFilePath;
	}
	
	/**
	 * This method is used for testing purposes. Alive checks
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello() {
		return "Hello REST PDP";
	}
	
	/**
	 * This method is used to convert the parameter decision to a string.
	 * 
	 * @param decision
	 * @return decision in a String format
	 */
	private String decisionParser(int decision) {
		switch (decision) {
		case 0:
			return "PERMIT";
		case 1:
			return "DENY";
		case 2:
			return "INDETERMINATE";
		case 3:
			return "NOT_APPLICABLE";
		default:
			throw new IllegalStateException("Unknown code");
		}
	}

	/**
	 * This method is used for the evaluation of the request made by the PEP The
	 * request needs to be parsed before being ran to evaluate(), 3 parameters
	 * needs to be extracted from the XACML request : userid, resourceid and
	 * actionid. This method returns the complete formated XACML response.
	 * 
	 * @param userid
	 * @param resourceid
	 * @param actionid
	 * @return formated XACML response
	 */
	private String evaluate(String userid, String resourceid, String actionid) {
		String response;
		String finalResult;
		Document document;
		ResponseCtx responseCtx;
		RequestType request;
		int decision;
		Set obligation;

		request = com.thalesgroup.pdp.core.XacmlProcessing.createRequest(
				userid, resourceid, actionid);
		responseCtx = myPdp.evaluate(request);
		Result result = (Result) responseCtx.getResults().iterator().next();

		// Decision + obligation
		decision = result.getDecision();
		obligation = result.getObligations();
		response = decisionParser(decision);

		document = XacmlProcessing.forgeXml(response, result.getStatus()
				.getCode().get(0).toString(), result.getStatus().getDetail(),
				obligation, xmlns);

		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		finalResult = sortie.outputString(document);

		return finalResult;
	}

	/**
	 * This method is used for the evaluation of the request made by the PEP The
	 * request doesn't needs to be parsed before being ran to evaluate(), it use
	 * the component RequestType from oasis. This method returns the complete
	 * formated XACML response.
	 * TODO: decrease NCSS
	 * 
	 * @param rqType
	 * @return formated XACML response

	 */
	private String evaluate(RequestType authZRequest) {

		LOGGER.debug("Received a XACML Request");
		long startTime = System.nanoTime();

		// Initialize Audit Part
		AuditLogs audit = AuditLogs.remove();
		audit = AuditLogs.getInstance();

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		audit.setDate(sdf.format(cal.getTime()));

		Request requestAudit = new Request();
		LOGGER.debug("Request informations : ");

		String subjectLogs = "|-|";
		String subjectid = null;

		// Parse subjects information
		LOGGER.debug("Subjects : ");
		List<Map<String, String>> subjList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < authZRequest.getSubject().size(); i++) {
			Map<String, String> subject = new HashMap<String, String>();
			SubjectType subjects = authZRequest.getSubject().get(i);
			for (AttributeType attr : subjects.getAttribute()) {
				subject.put(attr.getAttributeId(), attr.getAttributeValue()
						.get(0).getContent().get(0).toString());
				subjectLogs += attr.getAttributeId()
						+ "|"
						+ attr.getAttributeValue().get(0).getContent().get(0)
								.toString() + "|&|";
				if (attr.getAttributeId().equalsIgnoreCase(
						"urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
					subjectid = attr.getAttributeValue().get(0).getContent()
							.get(0).toString();
				}
				LOGGER.debug("Subject Attribute ID : " + attr.getAttributeId());
				LOGGER.debug("Subject Attribute Value : "
						+ attr.getAttributeValue().get(0).getContent().get(0)
								.toString());
			}
			subjList.add(subject);
		}

		// Parse resources information
		String resourceLogs = "|-|";

		List<Map<String, String>> rscList = new ArrayList<Map<String, String>>();
		LOGGER.debug("Resources : ");
		for (ResourceType rscType : authZRequest.getResource()) {
			Map<String, String> rsc = new HashMap<String, String>();
			for (AttributeType attr : rscType.getAttribute()) {
				rsc.put(attr.getAttributeId(), attr.getAttributeValue().get(0)
						.getContent().get(0).toString());
				resourceLogs += attr.getAttributeId()
						+ "|"
						+ attr.getAttributeValue().get(0).getContent().get(0)
								.toString() + "|&|";
				LOGGER.debug("Resource Attribute Id :" + attr.getAttributeId());
				LOGGER.debug("Resource Attribute Val :"
						+ attr.getAttributeValue().get(0).getContent().get(0));
			}
			rscList.add(rsc);
		}

		// Parse actions information
		LOGGER.debug("Actions : ");
		List<Map<String, String>> actList = new ArrayList<Map<String, String>>();
		Map<String, String> act = new HashMap<String, String>();
		for (AttributeType attr : authZRequest.getAction().getAttribute()) {
			act.put(attr.getAttributeId(), attr.getAttributeValue().get(0)
					.getContent().get(0).toString());
			LOGGER.debug("Action Attribute Id :" + attr.getAttributeId());
			LOGGER.debug("Action Attribute Val :"
					+ attr.getAttributeValue().get(0).getContent().get(0));
		}
		actList.add(act);

		requestAudit.setSubjects(subjList);
		requestAudit.setResources(rscList);
		requestAudit.setActions(actList);

		audit.setRequest(requestAudit);

		// Going to evaluate the request against the policy
		LOGGER.debug("Going to evaluate the request against policy(ies)");

		String response;
		String finalResult;
		Document document;
		ResponseCtx responseCtx;
		int decision;
		Set obligation;

		responseCtx = myPdp.evaluate(authZRequest);
		Result result = (Result) responseCtx.getResults().iterator().next();

		// Decision + obligation
		decision = result.getDecision();
		obligation = result.getObligations();
		response = decisionParser(decision);

		document = XacmlProcessing.forgeXml(response, result.getStatus()
				.getCode().get(0).toString(), result.getStatus().getDetail(),
				obligation, xmlns);

		XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
		finalResult = sortie.outputString(document);

		// Parse log information
		long endTime = System.nanoTime();

		Set<Entry<String, String>> set = audit.getRequest().getActions().get(0)
				.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();
		String action = null;
		while (i.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) i
					.next();
			action = entry.getKey() + "=" + entry.getValue();
		}

		audit.setEffect(response);
		String policyVersion = null;
		String policyId = null;
		if (audit.getMatchPolicies() != null
				&& !audit.getMatchPolicies().isEmpty()) {
			policyVersion = audit.getMatchPolicies().get(0).getPolicyVersion();
			policyId = audit.getMatchPolicies().get(0).getPolicyId();
		}

		Set<String> config = myStore.getSupportedPDPConfigurations();
		Iterator<String> it = config.iterator();
		while (it.hasNext()) {
			pdpID = (String) it.next();
		}

		String basicInfos = "|-|" + PEP_ID + "|" + pdpID + "|" + audit.getDate()
				+ "|" + audit.getEffect() + "|" + audit.getRuleId() + "|"
				+ policyId + "|" + policyVersion + "|" + action + "|"
				+ (endTime - startTime) + "|" + subjectid;

		// String basicInfos = null;
		// Store attributes resolved by the PDP for the specific request
		String attrResolved = "|-|";
		List<AttributesResolved> attrResolvedList = audit.getAttrResolv();
		if (attrResolvedList != null) {
			for (int l = 0; l < attrResolvedList.size(); l++) {
				attrResolved += attrResolvedList.get(l).getAttributeId()
						.toString()
						+ "|"
						+ attrResolvedList.get(l).getAttributeValue()
						+ "|&|";
			}
		}
		AUDIT_LOGGER.debug(basicInfos
				+ subjectLogs.substring(0, subjectLogs.length() - 3)
				+ resourceLogs.substring(0, resourceLogs.length() - 3)
				+ attrResolved.substring(0, attrResolved.length() - 3));

		LOGGER.info("Authorization result is : " + response);
		LOGGER.debug("Total elapsed time in execution of method callMethod() is :"
				+ (endTime - startTime));
		return finalResult;
	}

	/**
	 * 
	 * This method is called if GET method is used. Just for testing purposes.
	 * 
	 * @param userid
	 * @param resourceid
	 * @param actionid
	 * @return String
	 * 
	 */
	@Path("{user}/{resource}/{action}")
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String pdpXmlGetEval(@PathParam("user") String userid,
			@PathParam("resource") String resourceid,
			@PathParam("action") String actionid) {

		return evaluate(userid, resourceid, actionid);
	}

	/**
	 * 
	 * This method is called if POST method is used. It's the main method who
	 * grab the request from the PEP stored in a form or directly in a
	 * formParam. This method returns the properly formated XACML response
	 * directly to the PEP.
	 * 
	 * @param formRequest
	 * @param form
	 * @return formated XACML response
	 */
	@POST
	@Produces(MediaType.TEXT_XML)
	@Consumes(MediaType.APPLICATION_XML)
	public String pdpXmlPostXacmlEval(String request) throws IOException {
		String encoding;
			if ((myHttpHeaders != null) && (myHttpHeaders.getRequestHeader(HttpHeaders.ACCEPT_CHARSET) != null)) {
				encoding = myHttpHeaders.getRequestHeaders().getFirst(
						HttpHeaders.ACCEPT_CHARSET);
			} else {
				encoding = "UTF-8";
			}
			String myRequest = new String(request.getBytes(), encoding);

			return evaluate(XacmlProcessing.parseXmlRequestType(myRequest));
	}
	
	public HttpHeaders getMyHttpHeaders() {
		return myHttpHeaders;
	}

	public void setMyHttpHeaders(HttpHeaders myHttpHeaders) {
		this.myHttpHeaders = myHttpHeaders;
	}
}