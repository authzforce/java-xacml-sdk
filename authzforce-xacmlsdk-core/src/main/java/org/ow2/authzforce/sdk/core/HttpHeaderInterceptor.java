package org.ow2.authzforce.sdk.core;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class HttpHeaderInterceptor extends AbstractPhaseInterceptor<Message> {

	private MetadataMap<String, String> headers;

	public HttpHeaderInterceptor(String phase) {
		super(phase);
	}
	
	public HttpHeaderInterceptor() {
		super(Phase.PRE_PROTOCOL);
	}

	public HttpHeaderInterceptor(MetadataMap<String, String> customHeaders) {
		super(Phase.PRE_PROTOCOL);
		this.headers = customHeaders;
	}

	public void handleMessage(Message message) throws Fault {
		//System.out.println("HTTP HEADER INTERCEPTOR");
		try {
			((MetadataMap<String, String>) message.get(Message.PROTOCOL_HEADERS)).putAll(headers);
		} catch (Exception ce) {
			throw new Fault(ce);
		}
	}
}
