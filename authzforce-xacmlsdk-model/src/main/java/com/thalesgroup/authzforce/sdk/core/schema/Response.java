/**
 * Copyright (C) 2013-2014 Thales Services - ThereSIS - All rights reserved.
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

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.DecisionType;


public class Response {

	private List<Attribute> attributes;
	private DecisionType decision;
	
	public List<Attribute> getAttributes() {
		if(null == attributes) {
			this.attributes = new ArrayList<Attribute>();
		}
		return attributes;
	}
	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
	public String getSubject() {
		for (Attribute attribute : attributes) {
			if(attribute.getAttributeId().equals(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value())) {
				return String.valueOf(attribute.getAttributeValues().get(0).getContent().get(0));
			}
		}
		return null;
	}
	public void setSubject(String subject) {
		AttributeValueType attrValue = new AttributeValueType();
		attrValue.getContent().add(subject);
		Attribute attr = new Attribute();
		attr.setAttributeId(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value());		
		attr.getAttributeValues().add(attrValue);
		
		this.getAttributes().add(attr);
	}
	public void setResourceId(String resourceId) {
		AttributeValueType attrValue = new AttributeValueType();
		attrValue.getContent().add(resourceId);
		Attribute attr = new Attribute();
		attr.setAttributeId(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value());		
		attr.getAttributeValues().add(attrValue);
		
		this.getAttributes().add(attr);
	}	
	
	public String getResourceId() {
		for (Attribute attribute : attributes) {
			if(attribute.getAttributeId().equals(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value())) {
				return String.valueOf(attribute.getAttributeValues().get(0).getContent().get(0));
			}
		}
		return null;
	}
	public String getAction() {
		for (Attribute attribute : attributes) {
			if(attribute.getAttributeId().equals(XACMLAttributeId.XACML_ACTION_ACTION_ID.value())) {
				return String.valueOf(attribute.getAttributeValues().get(0).getContent().get(0));
			}
		}
		return null;
	}
	public void setAction(String action) {
		AttributeValueType attrValue = new AttributeValueType();
		attrValue.getContent().add(action);
		Attribute attr = new Attribute();
		attr.setAttributeId(XACMLAttributeId.XACML_ACTION_ACTION_ID.value());		
		attr.getAttributeValues().add(attrValue);
		
		this.getAttributes().add(attr);
	}
	
	public DecisionType getDecision() {
		return decision;
	}
	public void setDecision(DecisionType decision) {
		this.decision = decision;
	}
}