package org.ow2.authzforce.sdk.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.thalesgroup.authzforce.api.jaxrs.EndUserDomainSet;

public class PdpService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PdpService.class);
	private final static String ENDPOINT_ADDRESS = "http://127.0.0.1:7777/";
	private static Server server;

	public PdpService() {
		this.setUp();
	}

	private void setUp() {
		LOGGER.info("SETUP CONTEXT: " + this.getClass());

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(new String[]{"classpath:META-INF/spring/beans.xml"});
		JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();
		List<Object> serviceBeans = new ArrayList<Object>();
		serviceBeans.add(ctx.getBean("domainsResourceBean"));
		sf.setServiceBeans(serviceBeans);
		sf.setAddress(ENDPOINT_ADDRESS);
		sf.setResourceClasses(EndUserDomainSet.class);
		
		server = sf.create();

	}

	public static void destroy() throws Exception {
		if (server != null) {
			server.stop();
			server.destroy();
			server = null;
		}
		LOGGER.info("Stopping Server");
	}

}
