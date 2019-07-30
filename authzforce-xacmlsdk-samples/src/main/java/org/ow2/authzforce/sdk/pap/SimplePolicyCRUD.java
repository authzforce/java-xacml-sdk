package org.ow2.authzforce.sdk.pap;


import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import org.ow2.authzforce.rest.api.xmlns.DomainProperties;
import org.ow2.authzforce.sdk.AdminXacmlSdk;
import org.ow2.authzforce.sdk.impl.AdminXacmlSdkImpl;
import org.ow2.authzforce.sdk.utils.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.Link;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SimplePolicyCRUD {
    private static final Logger log = LoggerFactory.getLogger(SimplePolicyCRUD.class);

    private static final String DOMAIN = "myTestDomain1";

    public static void main(String[] args) throws Exception {
        URI baseURL = ServerSetup.getRootURL(ServerSetup.getServer());
        AdminXacmlSdk client = new AdminXacmlSdkImpl(baseURL);

        log.warn("***********domain CRUD operations***********");
        log.error("(C)reate");
        List<Link> domains = client.getDomains();
        log.info("{} domains found: {}", domains.size(), domains.stream().map(Link::getHref).toArray());
        String newDomainID = client.addDomain("test domain", DOMAIN);

        log.error("(R)ead");
        domains = client.getDomains();
        log.info("{} domains found: {}", domains.size(), domains.stream().map(Link::getHref).toArray());

        DomainProperties domainProperties = client.getDomain(newDomainID);
        log.info("Friendly domain name: {}", domainProperties.getExternalId());

        log.error("(U)pdate");
        client.addDomain("test domain", DOMAIN);

        log.error("(D)elete... lets leave it till the end, if we delete the domain now we cant to policy CRUD on it");

        log.warn("***********policy CRUD operations***********");
        log.error("(C)reate");
        PolicySet set = client.createSimplePolicy(newDomainID, "myTestPolicy", "Test policy", Collections.emptyList());
        client.addPolicy(newDomainID, set);
        log.info("The description of this policy is {}", client.getPolicy(newDomainID, null, "myTestPolicy").getDescription());
        log.error("(R)ead");
        log.info("policies in {} are {}", newDomainID, client.getPoliciesNames(newDomainID).stream().map(Link::getHref).toArray());

        log.info("getPolicy domain only");
        log.debug(client.getPolicy(newDomainID, null, null).toString());

        log.info("getPolicy domain + policyID");
        log.debug(client.getPolicy(newDomainID, null, "myTestPolicy").toString());

        log.info("getPolicy domain + policyID + version");
        log.debug(client.getPolicy(newDomainID, "1.0.0", "myTestPolicy").toString());

        log.info("getPolicies domain only");
        log.debug(Arrays.toString(client.getPolicies(newDomainID, null).toArray()));

        log.info("getPolicies domain + policy ID");
        log.debug(Arrays.toString(client.getPolicies(newDomainID, "myTestPolicy").toArray()));

        log.error("(U)pdate");
        set = client.createSimplePolicy(newDomainID, "myTestPolicy", "Test policy", Collections.emptyList());
        client.addPolicy(newDomainID, set);
        log.error("(D)elete");
        client.deletePolicy(newDomainID, null, "myTestPolicy");
        client.deleteDomain(newDomainID);
    }
}