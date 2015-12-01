package com.thalesgroup.authzforce.sdk.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thalesgroup.authzforce.sdk.core.schema.Attribute;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.xacml._3_0.identifiers.XACMLAttributeId;

public final class ResponsesFactory extends Responses {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ResponsesFactory.class);
	
	private String filterAttribute;
	
	public ResponsesFactory (Responses responses) {
		this.getResponses().addAll(responses.getResponses());
	}
	
	public Responses getResponseGroupBySubject() {
		return getResponseGroupBy(XACMLAttributeId.XACML_SUBJECT_SUBJECT_ID);		
	}
	
	public Responses getResponseGroupByResource() {
		return getResponseGroupBy(XACMLAttributeId.XACML_RESOURCE_RESOURCE_ID);		
	}
	
	public Responses getResponseGroupByAction() {
		return getResponseGroupBy(XACMLAttributeId.XACML_ACTION_ACTION_ID);		
	}
	
	private Responses getResponseGroupBy(XACMLAttributeId id2GroupBy) {
		this.filterAttribute = id2GroupBy.value();
		Response sortedResponses = new Response();
		Responses responses = new Responses();
		List<Response> arrayFinal = new ArrayList<Response>();
		for (Response response : this.getResponses()) {	
			for (Attribute attr : response.getAttributes()) {
				if(attr.getAttributeId().equals(id2GroupBy.value())) {
					sortedResponses.getAttributes().addAll(response.getAttributes());
					this.filterAttribute = String.valueOf(attr.getAttributeValues().get(0).getContent()); 
				}
			}
			arrayFinal.add(sortedResponses);
		}		
		
		responses.setResponses(arrayFinal);
		
		return responses;
	}
}