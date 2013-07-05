package com.thalesgroup.authzforce.sdk.core.schema;

import java.io.Serializable;
import java.util.List;

import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Resource extends AttributeType {

	public Resource(Object value, XACMLDatatypes xacmlDatatype) {
		AttributeValueType attrVal = new AttributeValueType();
		attrVal.getContent().add((Serializable)value);
		attrVal.setDataType(xacmlDatatype.value());
		
		this.includeInResult = true;
		this.setAttributeId(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID.value());
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
	
	@Override
	public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator,
			Object object, EqualsStrategy strategy) {
		return super.equals(thisLocator, thatLocator, object, strategy);
	}
}
