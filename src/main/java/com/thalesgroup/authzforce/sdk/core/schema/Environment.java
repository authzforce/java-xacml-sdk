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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Environment extends Attribute {

	public Environment(String value) {
		getInstance(value, XACMLDatatypes.XACML_DATATYPE_STRING);
	}

	public Environment(int value) {
		getInstance(String.valueOf(value),
				XACMLDatatypes.XACML_DATATYPE_INTEGER);
	}
	
	public Environment(double value) {
		getInstance(String.valueOf(value),
				XACMLDatatypes.XACML_DATATYPE_DOUBLE);
	}
	
	public Environment(boolean value) {
		getInstance(value, XACMLDatatypes.XACML_DATATYPE_STRING);
	}

	public Environment(Date value) {
		getInstance(new SimpleDateFormat("YYY-MM-DD").format(value),
				XACMLDatatypes.XACML_DATATYPE_DATE);
	}

	/**
	 * 
	 * @param value
	 * @param xacmlDatatype
	 * 
	 * @Deprecated It's now best to used constructor strongly typed to ensure
	 *             that the datatype is supported
	 */
	@Deprecated
	public Environment(Object value, XACMLDatatypes xacmlDatatype) {
		getInstance(value, xacmlDatatype);
	}
	
	private void getInstance(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		
		this.setAttributeId(XACMLAttributeId.XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID.value());
		this.getAttributeValues().add(attrVal);
	}
	
	public void addObject(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		this.getAttributeValues().add(attrVal);
	}

	public List getAttributes() {
		return this.attributeValues;
	}
}
