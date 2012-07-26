package com.thalesgroup.pdp.impl;

import java.io.File;
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

import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.DecisionType;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResponseType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResultType;
import oasis.names.tc.xacml._2_0.context.schema.os.StatusCodeType;
import oasis.names.tc.xacml._2_0.context.schema.os.StatusType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;
import oasis.names.tc.xacml._2_0.policy.schema.os.AttributeAssignmentType;
import oasis.names.tc.xacml._2_0.policy.schema.os.EffectType;
import oasis.names.tc.xacml._2_0.policy.schema.os.ObligationType;
import oasis.names.tc.xacml._2_0.policy.schema.os.ObligationsType;

import org.apache.log4j.PropertyConfigurator;

import com.sun.xacml.ConfigurationStore;
import com.sun.xacml.Obligation;
import com.sun.xacml.PDP;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.PolicyFinder;
import com.thalesgroup.authzforce.audit.AttributesResolved;
import com.thalesgroup.authzforce.audit.AuditLogs;
import com.thalesgroup.authzforce.audit.Request;


public class PDPWrapper {

	private PDP pdp;
	private ConfigurationStore store = null;
	private final static org.apache.log4j.Logger logger = org.apache.log4j.Logger
			.getLogger(PDPWrapper.class);
	private final static org.apache.log4j.Logger auditLogger = org.apache.log4j.Logger
			.getLogger("audit");
	String pdpID;
	String pepID = "PEP-RIA";
	private static PDPWrapper pdpWrapper;
	private final String propertiesFile = "authzforce.properties";

	protected PDPWrapper() {
		Properties properties = new Properties();
		try {
			properties.load(PDPWrapper.class.getClassLoader().getResourceAsStream(propertiesFile));
		} catch (IOException e) {
			logger.fatal("An exception occurred : " + e);
		}
		PropertyConfigurator.configure(properties.getProperty("logProperties"));
		File configFile = new File(properties.getProperty("configFile"));
		logger.info("PDP Initialization");
		try {
			store = new ConfigurationStore(configFile);
		} catch (ParsingException e) {
			logger.fatal("An exception occurred : " + e);
		}
		try {
			pdp = new PDP(store.getDefaultPDPConfig());
		} catch (UnknownIdentifierException e) {
			logger.fatal("An exception occurred : " + e);
		}
	}

	public synchronized static PDPWrapper getInstance() {
		if (pdpWrapper == null) {
			pdpWrapper = new PDPWrapper();
		}
		return pdpWrapper;
	}

	@SuppressWarnings("unchecked")
	public ResponseType xacmlAuthzDecision(RequestType xacmlAuthzDecisionRequest) {
		AuditLogs audit = AuditLogs.remove();
		audit = AuditLogs.getInstance();

		Calendar cal = Calendar.getInstance();

		String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss.SSS";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		audit.setDate(sdf.format(cal.getTime()));
		logger.debug("Received a XACML Request");
		long startTime = System.nanoTime();
		Request requestAudit = new Request();
		logger.debug("Request informations : ");

		String subjectLogs = "|-|";
		String subjectid = null;

		// Parse subjects informations
		logger.debug("Subjects : ");
		List<Map<String, String>> subjList = new ArrayList<Map<String, String>>();
		for (int i = 0; i < xacmlAuthzDecisionRequest.getSubject().size(); i++) {
			Map<String, String> subject = new HashMap<String, String>();
			SubjectType subjects = xacmlAuthzDecisionRequest.getSubject().get(i);
			for (AttributeType attr : subjects.getAttribute()) {
				subject.put(attr.getAttributeId(), attr.getAttributeValue().get(0).getContent().get(0).toString());
				subjectLogs += attr.getAttributeId()+ "|"+ attr.getAttributeValue().get(0).getContent().get(0).toString() + "|&|";
				if (attr.getAttributeId().equalsIgnoreCase("urn:oasis:names:tc:xacml:1.0:subject:subject-id")) {
					subjectid = attr.getAttributeValue().get(0).getContent().get(0).toString();
				}
				logger.debug("Subject Attribute ID : " + attr.getAttributeId());
				logger.debug("Subject Attribute Value : "+ attr.getAttributeValue().get(0).getContent().get(0).toString());
			}
			subjList.add(subject);
		}
		
		String resourceLogs = "|-|";
		// ¨Parse resources informations
		List<Map<String, String>> rscList = new ArrayList<Map<String, String>>();
		logger.debug("Resources : ");
		for (ResourceType rscType : xacmlAuthzDecisionRequest.getResource()) {
			Map<String, String> rsc = new HashMap<String, String>();
			for (AttributeType attr : rscType.getAttribute()) {
				rsc.put(attr.getAttributeId(), attr.getAttributeValue().get(0)
						.getContent().get(0).toString());
				resourceLogs += attr.getAttributeId()
						+ "|"
						+ attr.getAttributeValue().get(0).getContent().get(0)
								.toString() + "|&|";
				logger.debug("Resource Attribute Id :" + attr.getAttributeId());
				logger.debug("Resource Attribute Val :"
						+ attr.getAttributeValue().get(0).getContent().get(0));
			}
			rscList.add(rsc);
		}

		// Parse actions informations
		logger.debug("Actions : ");
		List<Map<String, String>> actList = new ArrayList<Map<String, String>>();
		Map<String, String> act = new HashMap<String, String>();
		for (AttributeType attr : xacmlAuthzDecisionRequest.getAction()
				.getAttribute()) {
			act.put(attr.getAttributeId(), attr.getAttributeValue().get(0)
					.getContent().get(0).toString());
			logger.debug("Action Attribute Id :" + attr.getAttributeId());
			logger.debug("Action Attribute Val :"
					+ attr.getAttributeValue().get(0).getContent().get(0));
		}
		actList.add(act);

		requestAudit.setSubjects(subjList);
		requestAudit.setResources(rscList);
		requestAudit.setActions(actList);

		audit.setRequest(requestAudit);
		// Going to evaluate the request against the policy
		logger.debug("Going to evaluate the request against policy(ies)");
		ResponseCtx response = pdp.evaluate(xacmlAuthzDecisionRequest);

		ResponseType xacmlResponse = new ResponseType();
		Result result = (Result) response.getResults().iterator().next();

		ResultType resultType = new ResultType();
		resultType.setResourceId(result.getResource());

		// Decision
		int decision = result.getDecision();
		switch (decision) {
		case 0:
			resultType.setDecision(DecisionType.PERMIT);
			break;
		case 1:
			resultType.setDecision(DecisionType.DENY);
			break;
		case 2:
			resultType.setDecision(DecisionType.INDETERMINATE);
			break;
		case 3:
			resultType.setDecision(DecisionType.NOT_APPLICABLE);
			break;
		default:
			throw new IllegalStateException("Unknown code");
		}

		Status status = result.getStatus();
		StatusType statusType = new StatusType();

		StatusCodeType statusCodeType = new StatusCodeType();
		List<?> statusList = status.getCode();
		if (statusList != null && statusList.size() > 0) {
			statusCodeType.setValue((String) statusList.get(0));
		}
		statusType.setStatusMessage(status.getMessage());
		statusType.setStatusCode(statusCodeType);
		resultType.setStatus(statusType);

		// Obligations
		Set<?> obligationsSet = result.getObligations();
		if (obligationsSet != null) {
			ObligationsType obligationsType = new ObligationsType();
			for (Object ob : obligationsSet) {
				if (!(ob instanceof Obligation)) {
					throw new Error("Obligation is not conformed");
				}
				Obligation obl = (Obligation) ob;
				ObligationType obType = new ObligationType();
				obType.setObligationId(obl.getId().toASCIIString());
				obType.setFulfillOn(EffectType.fromValue(Result.DECISIONS[obl
						.getFulfillOn()]));
				for (Object assignment : obl.getAssignments()) {
					if (assignment instanceof Attribute) {
						Attribute attribute = (Attribute) assignment;
						AttributeAssignmentType attributeAssignment = new AttributeAssignmentType();
						attributeAssignment.setAttributeId(attribute.getId()
								.toString());
						attributeAssignment.setDataType(attribute.getType()
								.toString());
						attributeAssignment.getContent().add(
								attribute.getValue().encode());
						obType.getAttributeAssignment()
								.add(attributeAssignment);
					}
				}
				obligationsType.getObligation().add(obType);
			}
			if (obligationsSet.size() > 0) {
				resultType.setObligations(obligationsType);
			}
		}
		xacmlResponse.getResult().add(resultType);
		long endTime = System.nanoTime();
		// audit = AuditLogs.getInstance();

		Set<Entry<String, String>> set = audit.getRequest().getActions().get(0)
				.entrySet();
		Iterator<Entry<String, String>> i = set.iterator();
		String action = null;
		while (i.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) i
					.next();
			action = entry.getKey() + "=" + entry.getValue();
		}

		audit.setEffect(resultType.getDecision().value());
		String policyVersion = null;
		String policyId = null;
		if (audit.getMatchPolicies() != null && !audit.getMatchPolicies().isEmpty()) {
			policyVersion = audit.getMatchPolicies().get(0).getPolicyVersion();
			policyId = audit.getMatchPolicies().get(0).getPolicyId();
		}

		Set<String> config = store.getSupportedPDPConfigurations();
		Iterator<String> it = config.iterator();
		while (it.hasNext()) {
			pdpID = (String) it.next();
		}

		String basicInfos = "|-|" + pepID + "|" + pdpID + "|" + audit.getDate()
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
		auditLogger.debug(basicInfos
				+ subjectLogs.substring(0, subjectLogs.length() - 3)
				+ resourceLogs.substring(0, resourceLogs.length() - 3)
				+ attrResolved.substring(0, attrResolved.length() - 3));

		logger.info("Authorization result is : "
				+ resultType.getDecision().value());
		logger.debug("Total elapsed time in execution of method callMethod() is :"
				+ (endTime - startTime));
		return xacmlResponse;

	}

	public String reloadPolicies() {
		try {
			PolicyFinder policyFinder = store.getDefaultPDPConfig()
					.getPolicyFinder();
			policyFinder.init();
			return "Ok";
		} catch (UnknownIdentifierException e) {
			logger.fatal("An exception occured: " + e);
			return "An exception occured: " + e;
		}
	}

}
