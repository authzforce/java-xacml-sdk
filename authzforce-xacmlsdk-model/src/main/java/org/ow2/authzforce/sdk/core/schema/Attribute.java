package org.ow2.authzforce.sdk.core.schema;

import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

public class Attribute extends oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute {

	protected static final String DATE_FORMAT = "yyyy-MM-dd";

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

	public static String getDateFormat() {
		return DATE_FORMAT;
	}
}
