package com.thalesgroup.authzforce.sdk.exceptions;


public enum XacmlSdkExceptionCodes {
	
	MISSING_SUBJECT("MissingSubject"), 
	MISSING_RESOURCE("MissingResource"),
	MISSING_ACTION("MissingAction"),
	MISSING_ENVIRONMENT("MissingEnvironment");
	
	private final String value;
	
	XacmlSdkExceptionCodes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XacmlSdkExceptionCodes fromValue(String v) {
        for (XacmlSdkExceptionCodes c: XacmlSdkExceptionCodes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
