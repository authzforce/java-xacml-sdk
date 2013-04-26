package com.thalesgroup.authzforce.sdk.core.schema;

import java.io.Serializable;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Attribute extends AttributeValueType {

	public void setValue(Object value) {
		this.content.add((Serializable)value);
	}
}
