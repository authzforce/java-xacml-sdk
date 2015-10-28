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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public final class Resource extends Attribute {

	public Resource(final String value) {
		getInstance(value, XACMLDatatypes.XACML_DATATYPE_STRING);
	}

	public Resource(final int value) {
		getInstance(String.valueOf(value),
				XACMLDatatypes.XACML_DATATYPE_INTEGER);
	}
	
	public Resource(final double value) {
		getInstance(String.valueOf(value),
				XACMLDatatypes.XACML_DATATYPE_DOUBLE);
	}
	
	public Resource(final boolean value) {
		getInstance(value, XACMLDatatypes.XACML_DATATYPE_STRING);
	}

	/**
	 * 
	 * @param date /!\ WARNING: date format needs to be "YYY-MM-DD" /!\
	 */
	public Resource(final Date date) {
		getInstance(new SimpleDateFormat("YYY-MM-DD").format(date),
				XACMLDatatypes.XACML_DATATYPE_DATE);
	}

	private void getInstance(final Object value, final XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		
		this.includeInResult = true;
		this.setAttributeId(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value());
		this.getAttributeValues().add(attrVal);
	}
}
