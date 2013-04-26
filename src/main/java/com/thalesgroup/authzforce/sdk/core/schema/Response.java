package com.thalesgroup.authzforce.sdk.core.schema;

import java.util.ArrayList;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;

//import oasis.names.tc.xacml._2_0.context.schema.os.DecisionType;


public class Response {

	private List<String> resourceId = new ArrayList<String>();
	private String action;
	private String subject;
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setResourceId(List<String> resourceId) {
		this.resourceId = resourceId;
	}
	private DecisionType decision;
	
	public List<String> getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId.add(resourceId);
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public DecisionType getDecision() {
		return decision;
	}
	public void setDecision(DecisionType decision) {
		this.decision = decision;
	}
	
	
}