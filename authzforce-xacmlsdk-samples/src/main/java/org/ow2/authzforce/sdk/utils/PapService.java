package org.ow2.authzforce.sdk.utils;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import org.ow2.authzforce.sdk.AdminXacmlSdk;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.ow2.authzforce.sdk.impl.AdminXacmlSdkImpl;

import java.net.URI;
import java.util.Collections;
import java.util.UUID;

public class PapService {
    public static String setupBasicDomain(URI serverURI, String externalID) throws XacmlSdkException {
        AdminXacmlSdk sdk = new AdminXacmlSdkImpl(serverURI);
        String domainID = sdk.addDomain("test_domain", externalID);
        PolicySet policySet = sdk.createSimplePolicy(domainID, UUID.randomUUID().toString(), "test policy", Collections.emptyList());
        sdk.addPolicy(domainID, policySet);
        return domainID;
    }
}
