/**
 * Copyright (C) 2013-2014 Thales Services - ThereSIS - All rights reserved.
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
package com.thalesgroup.authzforce.sdk.exceptions;


public enum XacmlSdkExceptionCodes {
	
	MISSING_SUBJECT("MissingSubject"), 
	MISSING_RESOURCE("MissingResource"),
	MISSING_ACTION("MissingAction"),
	MISSING_ENVIRONMENT("MissingEnvironment"),
	CATEGORY_IS_NULL("NullCategory");
	
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
