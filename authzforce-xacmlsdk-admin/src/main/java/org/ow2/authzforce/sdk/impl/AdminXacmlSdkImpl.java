package org.ow2.authzforce.sdk.impl;

import com.github.zafarkhaja.semver.Version;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Target;
import org.ow2.authzforce.rest.api.jaxrs.PolicyVersionResource;
import org.ow2.authzforce.rest.api.xmlns.DomainProperties;
import org.ow2.authzforce.sdk.AdminXacmlSdk;
import org.ow2.authzforce.sdk.core.AdminNet;
import org.ow2.authzforce.sdk.exceptions.XacmlSdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.Link;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.core.MultivaluedMap;
import java.math.BigInteger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class AdminXacmlSdkImpl implements AdminXacmlSdk {
    private static final Logger LOG = LoggerFactory.getLogger(AdminXacmlSdkImpl.class);
    private final AdminNet adminNetworkHandler;

    public AdminXacmlSdkImpl(URI serverEndpoint, MultivaluedMap<String, String> customHeaders, boolean doDomainIdTranslation) {
        adminNetworkHandler = new AdminNet(serverEndpoint, customHeaders, doDomainIdTranslation);
    }

    public AdminXacmlSdkImpl(URI serverEndpoint, MultivaluedMap<String, String> customHeaders) {
        this(serverEndpoint, customHeaders, false);
    }

    public AdminXacmlSdkImpl(URI serverEndpoint) {
        this(serverEndpoint, null);
    }

    @Override
    public List<Link> getDomains(String... domains) {
        if (domains.length > 0) {
            List<Link> links = new ArrayList<>();
            for (String domain : domains) {
                links.addAll(adminNetworkHandler.getDomainsResource().getDomains(domain).getLinks());
            }
            return links;
        }

        return adminNetworkHandler.getDomainsResource().getDomains(null).getLinks();
    }

    @Override
    public DomainProperties getDomain(String domain) throws XacmlSdkException {
        try {
            return adminNetworkHandler.getDomainResource(domain).getDomain().getProperties();
        } catch (javax.ws.rs.NotFoundException e) {
            LOG.error("No such domain {}", domain);
            throw new XacmlSdkException("No such domain", e);
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }

    @Override
    public String addDomain(String description, String externalID) throws XacmlSdkException {
        try {
            LOG.info("adding domain {},{}", externalID, description);
            return adminNetworkHandler.getDomainsResource().addDomain(new DomainProperties(description, externalID)).getHref();
        } catch (javax.ws.rs.BadRequestException e) {
            if (getDomains(externalID).size() < 1)
                throw new XacmlSdkException(e);
            LOG.warn("A domain with external id {} already exists. Returning it", externalID);
            return getDomains(externalID).get(0).getHref();
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }

    @Override
    public void deleteDomain(String domain) throws XacmlSdkException {
        try {
            adminNetworkHandler.getDomainResource(domain).deleteDomain();
        } catch (javax.ws.rs.NotFoundException e) {
            LOG.error("No such domain {}", domain);
            throw new XacmlSdkException("No such domain", e);
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }


    @Override
    public List<Link> getPoliciesNames(String domain) throws XacmlSdkException {
        try {
            return adminNetworkHandler.getDomainResource(domain).getPapResource().getPoliciesResource().getPolicies().getLinks();
        } catch (javax.ws.rs.NotFoundException e) {
            LOG.error("No such domain {}", domain);
            throw new XacmlSdkException("No such domain", e);
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }

    @Override
    public PolicySet getPolicy(String domain, String version, String policyID) throws XacmlSdkException {
        try {
            return getPolicyVersionResource(domain, version, policyID).getPolicyVersion();
        } catch (javax.ws.rs.NotFoundException e) {
            LOG.error("No such policy {}@{} at domain {}", policyID, version, domain);
            throw new XacmlSdkException("No such policy", e);
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }

    @Override
    public Link addPolicy(String domain, PolicySet policySet) throws XacmlSdkException {
        LOG.debug("adding policy to {}: {}", domain, policySet);
        try {
            return adminNetworkHandler.getDomainResource(domain).getPapResource().getPoliciesResource().addPolicy(policySet);
        } catch (ClientErrorException e) {
            if (e.getLocalizedMessage().contains("409")) {
                LOG.error("Policy {}:{} already exists for domain {}", policySet.getPolicySetId(), policySet.getVersion(), domain);
                throw new XacmlSdkException("This policy already exists");
            }
            throw e;
        } catch (Exception e) {
            throw new XacmlSdkException(e);
        }
    }

    @Override
    public void deletePolicy(String domain, String version, String policyID) throws XacmlSdkException {
        LOG.debug("Deleting policy {}@{} from {}", policyID, version, domain);
        getPolicyVersionResource(domain, version, policyID).deletePolicyVersion();
    }

    @Override
    public PolicySet createSimplePolicy(String domain, String policyID, String description, List<Object> data) throws XacmlSdkException {
        LOG.debug("Creating policy {} for {}", policyID, domain);
        if (getPoliciesNames(domain).stream().anyMatch(link -> link.getHref().contains(policyID))) {
            PolicySet existingSet = getPolicyVersionResource(domain, "", policyID).getPolicyVersion();
            if (existingSet != null) {
                String newVersion = Version.valueOf(existingSet.getVersion()).incrementPatchVersion().toString();
                LOG.warn("Policy {} already exists for domain {}! Creating new version. Was {} now {}", policyID, domain, existingSet.getVersion(), newVersion);
                return new PolicySet(description, existingSet.getPolicyIssuer(), existingSet.getPolicySetDefaults(), existingSet.getTarget(), data, existingSet.getObligationExpressions(), existingSet.getAdviceExpressions(), policyID, newVersion, existingSet.getPolicyCombiningAlgId(), existingSet.getMaxDelegationDepth());
            }
        }
        String DENY_UNLESS_PERMIT = "urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:deny-unless-permit";//TODO should we depend on core just for this?
        return new PolicySet(description, null, null, new Target(null), data, null, null, policyID, Version.forIntegers(1).toString(), DENY_UNLESS_PERMIT, BigInteger.TEN);
    }

    @Override
    public List<PolicySet> getPolicies(String domain, String policyID) throws XacmlSdkException {
        LOG.info("Getting policies for domain {} policyID {}", domain, policyID);
        List<PolicySet> policies = new ArrayList<>();
        if (policyID == null || policyID.isEmpty()) {
            LOG.warn("No policy ID specified, will try all (this is expensive so might take a while...)");
            getPoliciesNames(domain).forEach(policy -> {
                adminNetworkHandler.getDomainResource(domain).getPapResource().getPoliciesResource().getPolicyResource(policy.getHref()).getPolicyVersions().getLinks().forEach(version -> {
                    try {
                        policies.add(getPolicy(domain, version.getHref(), policy.getHref()));
                    } catch (XacmlSdkException e) {
                        e.printStackTrace();
                    }
                });
            });
        } else {
            adminNetworkHandler.getDomainResource(domain).getPapResource().getPoliciesResource().getPolicyResource(policyID).getPolicyVersions().getLinks().forEach(version -> {
                try {
                    policies.add(getPolicy(domain, version.getHref(), policyID));
                } catch (XacmlSdkException e) {
                    e.printStackTrace();
                }
            });
        }
        return policies;
    }

    private PolicyVersionResource getPolicyVersionResource(String domain, String version, String policyID) throws XacmlSdkException {
        if (version == null || version.isEmpty()) {
            LOG.warn("No policy version specified, assuming latest");
            version = "latest";
        }
        if (policyID == null || policyID.isEmpty()) {
            LOG.warn("No policy ID specified, assuming root");
            policyID = getPoliciesNames(domain).get(0).getHref();
        }
        return adminNetworkHandler.getDomainResource(domain).getPapResource().getPoliciesResource().getPolicyResource(policyID).getPolicyVersionResource(version);
    }

}
