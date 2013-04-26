package com.thalesgroup.authzforce.sdk.core.schema;

import java.io.Serializable;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Action extends AttributeType {

	public Action(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());

		this.includeInResult = true;
		this.setAttributeId(XACMLAttributeId.XACML_ACTION_ACTION_ID.value());
		this.getAttributeValue().add(attrVal);
	}
	
	public void addObject(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		this.getAttributeValue().add(attrVal);
	}

	public List getAttributes() {
		return this.attributeValue;
	}
}
