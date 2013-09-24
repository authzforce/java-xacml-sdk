/**
 * Copyright (C) 2013-2013 Thales Services - ThereSIS - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
