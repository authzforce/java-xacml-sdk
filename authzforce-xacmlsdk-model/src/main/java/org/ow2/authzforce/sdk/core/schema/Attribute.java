package com.thalesgroup.authzforce.sdk.core.schema;

import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Attribute extends oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute {
	
	public Attribute(final List<AttributeValueType> attributeValues, final String attributeId, final String issuer, final boolean includeInResult) {
		super(attributeValues, attributeId, issuer, includeInResult);
	}
	
	@Override
	public void setAttributeId(String value) {
		try {
			XACMLAttributeId.fromValue(value);
			
		} catch (IllegalArgumentException e) {		
		}
		super.setAttributeId(value);
	}

}
