package org.ow2.authzforce.sdk.test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.PolicySet;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.ow2.authzforce.sdk.AdminXacmlSdk;
import org.ow2.authzforce.sdk.impl.AdminXacmlSdkImpl;
import org.ow2.authzforce.sdk.impl.AdminXacmlSdkImplTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3._2005.atom.Link;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.recordSpec;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@Ignore
public class ScenarioRecorder {
    public static final int PORT = 6666;
    public static final String ENDPOINT_ADDRESS = "http://127.0.0.1:" + PORT + "/";
    private static final String REAL_SERVER = "http://127.0.0.1:8080/authzforce-ce";
    private static final AdminXacmlSdk client = new AdminXacmlSdkImpl(URI.create(ENDPOINT_ADDRESS));
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminXacmlSdkImplTest.class);
    @Rule
    public WireMockRule server = new WireMockRule(options().port(PORT), false);

    private void happyPath() {
        LOGGER.info("Recording: Happy path");
        try {
            List<Link> domains = client.getDomains();
            LOGGER.info("getDomains():{}", domains);
            String newDomainID = client.addDomain("test domain", "myTestDomain1");
            LOGGER.info("addDomain():{}", newDomainID);
            LOGGER.info("getDomain():{}", client.getDomain(newDomainID));
            PolicySet set = client.createSimplePolicy(newDomainID, "myTestPolicy", "Test policy", Collections.emptyList());
            LOGGER.info("createSimplePolicy():{}", set);
            LOGGER.info("addPolicy():{}", client.addPolicy(newDomainID, set));
            set = client.getPolicy(newDomainID, null, "myTestPolicy");
            LOGGER.info("getPolicy():{}", set);
            client.deletePolicy(newDomainID, null, "myTestPolicy");
            client.deleteDomain(newDomainID);
            LOGGER.info("happy scenario done!");
        } catch (Exception e) {
            LOGGER.error("Happy scenario failure: {}", e.getMessage(), e);
        }

    }

    @Test
    @Ignore //Run manually once to setup wiremock
    public void rec() {
        try {
            LOGGER.info("start recording");
            server.start();
            server.resetAll();
            Files.list(Paths.get("src/test/resources/mappings")).forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (IOException e) {
                    LOGGER.warn("w {}", e.getMessage());
                }
            });
            server.startRecording(recordSpec().forTarget(REAL_SERVER).matchRequestBodyWithEqualToXml().ignoreRepeatRequests());

            happyPath();

            server.stopRecording();
            server.stop();
            LOGGER.info("done recording");
        } catch (Throwable t) {
            LOGGER.error("Error {}", t.getMessage());
            t.printStackTrace();
            System.exit(-1);
        }
    }
}
