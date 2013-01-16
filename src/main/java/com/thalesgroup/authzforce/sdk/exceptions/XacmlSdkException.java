package com.thalesgroup.authzforce.sdk.exceptions;

public class XacmlSdkException extends Exception {

	private static final long serialVersionUID = 985260175957642427L;

	public XacmlSdkException() {
		// TODO Auto-generated constructor stub
	}

	public XacmlSdkException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated constructor stub
	}

	public XacmlSdkException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
