package com.thalesgroup.authzforce.sdk.core.schema;

public enum XACMLDatatypes {
	
	XACML_DATATYPE_STRING("http://www.w3.org/2001/XMLSchema#string"),
	XACML_DATATYPE_BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean"),
	XACML_DATATYPE_INTEGER("http://www.w3.org/2001/XMLSchema#integer"),
	XACML_DATATYPE_DOUBLE("http://www.w3.org/2001/XMLSchema#double"),
	XACML_DATATYPE_TIME("http://www.w3.org/2001/XMLSchema#time"),
	XACML_DATATYPE_DATE("http://www.w3.org/2001/XMLSchema#date"),
	XACML_DATATYPE_DATETIME("http://www.w3.org/2001/XMLSchema#date-time"),
	XACML_DATATYPE_DAYTIME_DURATION("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration"),
	XACML_DATATYPE_YEARMONTH_DURATION("http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration"),
	XACML_DATATYPE_ANY_URI("http://www.w3.org/2001/XMLSchema#anyURI"),
	XACML_DATATYPE_HEX_BINARY("http://www.w3.org/2001/XMLSchema#hexBinary"),
	XACML_DATATYPE_BASE64_BINARY("http://www.w3.org/2001/XMLSchema#base64Binary"),
	XACML_DATATYPE_RFC822_NAME("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name"),
	XACML_DATATYPE_X500_NAME("urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
	
	private final String value;
	
	public String value() {
        return value;
    }
	
	private XACMLDatatypes(String v) {
        value = v;
    }

    public static XACMLDatatypes fromValue(String v) {
        for (XACMLDatatypes c: XACMLDatatypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
