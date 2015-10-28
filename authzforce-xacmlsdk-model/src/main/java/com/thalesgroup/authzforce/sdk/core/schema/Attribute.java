package com.thalesgroup.authzforce.sdk.core.schema;

public class Attribute extends oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute {
	
	@Override
	public void setAttributeId(String value) {
		try {
			XACMLAttributeId.fromValue(value);
			
		} catch (IllegalArgumentException e) {		
		}
		super.setAttributeId(value);
	}

}
