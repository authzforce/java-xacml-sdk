package com.thalesgroup.authzforce.sdk.core.schema;

import oasis.names.tc.xacml._2_0.context.schema.os.DecisionType;


public class Response {

	private String resourceId;
	private String action;
	private DecisionType decision;
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
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