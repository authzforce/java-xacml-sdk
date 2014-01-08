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

public class XacmlSdkException extends Exception {

	private static final long serialVersionUID = 985260175957642427L;

	public XacmlSdkException() {
		super();
	}

	public XacmlSdkException(String message) {
		super(message);
	}

	public XacmlSdkException(XacmlSdkExceptionCodes errorCode) {
		switch (errorCode) {
		case MISSING_SUBJECT:
			System.err.println(errorCode.value()+": You MUST put a subject into your request");
			break;
		case MISSING_ACTION:
			System.err.println(errorCode.value()+": You MUST put a ACTION into your request");
			break;
		case MISSING_RESOURCE:
			System.err.println(errorCode.value()+": You MUST put a RESOURCE into your request");			
			break;
		default:
			System.err.print(errorCode.value()+": Error code unknown");
			break;
		}
		this.printStackTrace();
	}

	public XacmlSdkException(Throwable cause) {
		super(cause);
	}

	public XacmlSdkException(String message, Throwable cause) {
		super(message, cause);
	}

}
