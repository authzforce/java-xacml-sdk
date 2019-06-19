package org.ow2.authzforce.sdk.core;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.ow2.authzforce.rest.api.jaxrs.DomainResource;
import org.ow2.authzforce.rest.api.jaxrs.DomainsResource;
import org.ow2.authzforce.rest.api.xmlns.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.Link;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;

public class Net {

	private static final Logger LOGGER = LoggerFactory.getLogger(Net.class);

	private MetadataMap<String, String> customHeaders;
	private String domainId;
	private URI serverEndpoint;
	private DomainResource targetedDomain;

	public Net(URI serverEndpoint, String domainId, MultivaluedMap<String, String> headers, boolean doDomainIdTranslation) {

		this.serverEndpoint = serverEndpoint;
		this.domainId = domainId;
		this.customHeaders = new MetadataMap<>(headers);
		this.targetedDomain = setupProxy(doDomainIdTranslation);
	}

	private DomainResource setupProxy(boolean doDomainIdTranslation) {
		DomainsResource domainsResource = JAXRSClientFactory.create(String.valueOf(this.serverEndpoint), DomainsResource.class);
		if (doDomainIdTranslation) {
			this.domainId = getDomainIdFromAlias(domainsResource, this.domainId);
		}

		DomainResource proxy = domainsResource.getDomainResource(this.domainId);
		
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

	private String getDomainIdFromAlias(DomainsResource domainsResource, String domainAlias) {
		Resources resources = domainsResource.getDomains(domainAlias);
		String domainID = "";
		if (resources.getLinks().size() < 1) {
			LOGGER.warn("there are no domains with external reference {}", domainAlias);
			return domainID;
		}
		if (resources.getLinks().size() > 1) {
			LOGGER.warn("More than 1 link for resource {}. Will select first available", domainAlias);
		}
		Link link = resources.getLinks().get(0);
		if (link != null) {
			domainID = link.getHref();
			LOGGER.debug("Resolved {} to {}", domainAlias, domainID);
		}
		return domainID;
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
