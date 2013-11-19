/**
 * Copyright (C) 2013-2013 Thales Services - ThereSIS - All rights reserved.
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

import java.io.Serializable;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;

public class Subject extends Attribute {

	public Subject(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		
		this.includeInResult = true;
		this.setAttributeId(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID.value());
		this.getAttributeValues().add(attrVal);
	}
	
	/**
	 * 
	 * @param value
	 * @param xacmlDatatype
	 */
	public void addObject(Object value, XACMLDatatypes xacmlDatatype) {
		this.addObject(value, xacmlDatatype, XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID);
	}
	
	/**
	 * 
	 * @param value
	 * @param xacmlDatatype
	 * @param attributeId
	 */
	public void addObject(Object value, XACMLDatatypes xacmlDatatype, XACMLAttributeId attributeId) {
		this.addObject(value, xacmlDatatype, attributeId.value());
	}
	
	/**
	 * 
	 * @param value
	 * @param xacmlDatatype
	 * @param attributeId
	 */
	public void addObject(Object value, XACMLDatatypes xacmlDatatype, String attributeId) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());		
		this.getAttributeValues().add(attrVal);
		this.setAttributeId(attributeId);
	}

	public List getAttributes() {
		return this.attributeValues;
	}

}
