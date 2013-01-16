package com.thalesgroup.authzforce.sdk.core.schema;

import java.util.List;

public class Environment extends Attribute {


	private List<Attribute> attribute;
	
	public List<Attribute> getAttribute() {
		return attribute;
	}

	public void setAttribute(List<Attribute> attribute) {
		this.attribute = attribute;
	}

	public Environment(String value, XACMLDatatypes datatype) {
		this.setId(XACMLAttributeId.XACML_ENVIRONMENT_ENVIRONMENT_ID);
		this.setValue(value);
		this.setDatatype(datatype);
	}

}
