package org.ow2.authzforce.sdk.core;

import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.ow2.authzforce.rest.api.jaxrs.DomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Net {

	private static final Logger LOGGER = LoggerFactory.getLogger(Net.class);

	private MetadataMap<String, String> customHeaders;
	private String domainId;
	private URI serverEndpoint;
	private DomainResource targetedDomain;

	public Net(URI serverEndpoint, String domainId, MultivaluedMap<String, String> headers) {
		this.serverEndpoint = serverEndpoint;
		this.domainId = domainId;
		this.customHeaders = new MetadataMap<String, String>(headers);
		this.targetedDomain = setupProxy();		
	}

	private DomainResource setupProxy() {
		DomainResource proxy = JAXRSClientFactory.create(String.valueOf(this.serverEndpoint), DomainResource.class);
		
		LOGGER.debug("Adding custom headers {}", this.customHeaders.toString());
		final ClientConfiguration clientConf = WebClient.getConfig(WebClient.client(proxy));
		final HttpHeaderInterceptor headerInterceptor = new HttpHeaderInterceptor(this.customHeaders);
		clientConf.getOutInterceptors().add(headerInterceptor);
		
		// Request/response logging (for debugging).
		if (LOGGER.isDebugEnabled()) {
			clientConf.getInInterceptors().add(new LoggingInInterceptor());
			clientConf.getOutInterceptors().add(new LoggingOutInterceptor());
		}

		return proxy;
	}

	/*
	 * Headers customizers
	 */

	public MultivaluedMap<String, String> getCustomHeaders() {
		return customHeaders;
	}

	public DomainResource getMyDomainResource() {
		return targetedDomain;
	}

	@Override
	public String toString() {

		String networkHandler = "[ endpoint => " + this.serverEndpoint + ", domain => " + this.domainId;
		if (WebClient.client(targetedDomain) != null) {
			networkHandler += ", headers => [" + WebClient.client(targetedDomain).getHeaders() + "]]";
		} else {
			networkHandler += "]";
		}
		return networkHandler;
	}

}
