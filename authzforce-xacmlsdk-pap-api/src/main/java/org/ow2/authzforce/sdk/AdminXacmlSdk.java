package org.ow2.authzforce.sdk;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import org.ow2.authzforce.rest.api.xmlns.DomainProperties;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.w3._2005.atom.Link;

import java.util.List;

public interface AdminXacmlSdk {
    List<Link> getDomains(String... domains);

    DomainProperties getDomain(String domain) throws XacmlSdkException;

    String addDomain(String description, String externalID) throws XacmlSdkException;

    void deleteDomain(String domain) throws XacmlSdkException;

    List<Link> getPoliciesNames(String domain) throws XacmlSdkException;

    PolicySet getPolicy(String domain, String version, String policyID) throws XacmlSdkException;

    Link addPolicy(String domain, PolicySet policySet) throws XacmlSdkException;

    void deletePolicy(String domain, String version, String policyID) throws XacmlSdkException;

    PolicySet createSimplePolicy(String domain, String policyID, String description, List<Object> data) throws XacmlSdkException;

    List<PolicySet> getPolicies(String domain, String policyID) throws XacmlSdkException;
}
