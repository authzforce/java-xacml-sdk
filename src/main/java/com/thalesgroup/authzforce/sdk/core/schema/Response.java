/**
 * Copyright (C) ${h_inceptionYear}-2013 ${h_copyrightOwner} - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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