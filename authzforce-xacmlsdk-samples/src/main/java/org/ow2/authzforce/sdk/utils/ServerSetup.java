package org.ow2.authzforce.sdk.utils;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class ServerSetup {
    private static final String IMAGE_NAME = "authzforce/server:release-8.1.0";
    private static final int PORT = 8080;

    public static GenericContainer getServer() {
        GenericContainer server = new GenericContainer(IMAGE_NAME)
                .withExposedPorts(PORT)
                .waitingFor(Wait.forLogMessage(".*Server startup.*",1));

        server.start();
        return server;
    }

    public static URI getRootURL(GenericContainer server) {
        return UriBuilder.fromUri("/authzforce-ce")
                .scheme("http")
                .host(server.getContainerIpAddress())
                .port(server.getMappedPort(PORT))
                .build();
    }
}
