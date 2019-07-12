package org.ow2.authzforce.sdk.core;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.ow2.authzforce.rest.api.jaxrs.DomainResource;
import org.ow2.authzforce.rest.api.jaxrs.DomainsResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class AdminNet {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminNet.class);
    private final boolean doDomainIdTranslation;
    private final DomainsResource domainsResource;
    private final MetadataMap<String, String> customHeaders;
    private final URI serverEndpoint;
    private Map<String, Net> domainHandlers = new HashMap<>();


    public AdminNet(URI serverEndpoint, MultivaluedMap<String, String> headers, boolean doDomainIdTranslation) {
        this.serverEndpoint = serverEndpoint;
        this.customHeaders = new MetadataMap<>(headers);
        this.doDomainIdTranslation = doDomainIdTranslation;
        this.domainsResource = setupProxy();
    }

    public DomainsResource getDomainsResource() {
        return this.domainsResource;
    }

    private DomainsResource setupProxy() {
        DomainsResource domainsResource = JAXRSClientFactory.create(String.valueOf(this.serverEndpoint), DomainsResource.class);

        LOGGER.debug("Adding custom headers {}", this.customHeaders.toString());
        final ClientConfiguration clientConf = WebClient.getConfig(WebClient.client(domainsResource));
        final HttpHeaderInterceptor headerInterceptor = new HttpHeaderInterceptor(this.customHeaders);
        clientConf.getOutInterceptors().add(headerInterceptor);

        // Request/response logging (for debugging).
        if (LOGGER.isDebugEnabled()) {
            clientConf.getInInterceptors().add(new LoggingInInterceptor());
            clientConf.getOutInterceptors().add(new LoggingOutInterceptor());
        }
        return domainsResource;
    }

    public DomainResource getDomainResource(String domain) {
        return getDomainHandler(domain).getMyDomainResource();
    }

    private Net getDomainHandler(String domain) {
        if (!domainHandlers.containsKey(domain)) {
            domainHandlers.put(domain, new Net(serverEndpoint, domain, customHeaders, doDomainIdTranslation));
        }
        return domainHandlers.get(domain);
    }

}
