package org.ow2.authzforce.sdk.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.ow2.authzforce.rest.api.xmlns.DomainProperties;
import org.ow2.authzforce.sdk.AdminXacmlSdk;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.Link;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.ow2.authzforce.sdk.test.ScenarioRecorder.ENDPOINT_ADDRESS;
import static org.ow2.authzforce.sdk.test.ScenarioRecorder.PORT;

public class AdminXacmlSdkImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminXacmlSdkImplTest.class);
    private static final String DOMAIN = "myTestDomain1";
    @Rule
    public WireMockRule server = new WireMockRule(options().port(PORT), false);
    private AdminXacmlSdk client;
    private String newDomainID = "dP79Bab_EemeoQJCrBEAAg";

    @Before
    public void setup() {
        client = new AdminXacmlSdkImpl(URI.create(ENDPOINT_ADDRESS));
    }

    @Test
    public void domainCrud() throws XacmlSdkException {
        List<Link> domains = client.getDomains();
        assertFalse(domains.isEmpty());
        newDomainID = client.addDomain("test domain", DOMAIN);
        LOGGER.info("new domain {}", newDomainID);
        DomainProperties domainProperties = client.getDomain(newDomainID);
        assertEquals(domainProperties.getExternalId(), DOMAIN);
        client.deleteDomain(newDomainID);
    }

    @Test
    public void policyCrud() throws XacmlSdkException {
        PolicySet set = client.createSimplePolicy(newDomainID, "myTestPolicy", "Test policy", Collections.emptyList());
        client.addPolicy(newDomainID, set);
        client.getPolicy(newDomainID, null, "myTestPolicy");
        client.deletePolicy(newDomainID, null, "myTestPolicy");
    }
}