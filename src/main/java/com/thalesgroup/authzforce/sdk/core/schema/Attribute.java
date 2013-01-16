package com.thalesgroup.authzforce.sdk.core.schema;

public abstract class Attribute {

	private XACMLAttributeId id;	
	private XACMLDatatypes datatype;
	private String value;
	
	public XACMLAttributeId getId() {
		return id;
	}
	public void setId(XACMLAttributeId id) {
		this.id = id;
	}
	public XACMLDatatypes getDatatype() {
		return datatype;
	}
	public void setDatatype(XACMLDatatypes datatype) {
		this.datatype = datatype;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
