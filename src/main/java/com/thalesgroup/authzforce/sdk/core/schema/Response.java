package com.thalesgroup.authzforce.sdk.core.schema;

import java.util.HashMap;
import java.util.Map;

public class Response {

	private Map<String, String> myResponses;
	
	public Response() {
		myResponses = new HashMap<String, String>();
	}
	
	public void setResponse(String resourceId, String decision) {
		myResponses.put(resourceId, decision);
	}

	public Map<String, String> getResponses() {
		return myResponses;
	}
}