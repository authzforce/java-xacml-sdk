package com.thalesgroup.authzforce.sdk.tests.utils;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.thalesgroup.authzforce.core.XACMLBindingUtils;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Request;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Response;

public final class Utils {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);
	
	/**
	 * This creates the XACML request from file on classpath
	 * 
	 * @param requestFileLocation
	 *            file path (with Spring-supported URL prefixes: 'classpath:', etc.) path to the
	 *            request file, relative to classpath
	 * @return the XML/JAXB Request or null if any error
	 * @throws JAXBException
	 *             error reading XACML 3.0 Request from the file at {@code requestFileLocation}
	 * @throws FileNotFoundException
	 *             no file found at {@code requestFileLocation}
	 */
	public static Request createRequest(String requestFileLocation) throws JAXBException, FileNotFoundException
	{
		/**
		 * Get absolute path/URL to request file in a portable way, using current class loader. As
		 * per javadoc, the name of the resource passed to ClassLoader.getResource() is a
		 * '/'-separated path name that identifies the resource. So let's build it. Note: do not use
		 * File.separator as path separator, as it will be turned into backslash "\\" on Windows,
		 * and will be URL-encoded (%5c) by the getResource() method (not considered path separator
		 * by this method), and file will not be found as a result.
		 */
		URL requestFileURL = ResourceUtils.getURL(requestFileLocation);
		if (requestFileURL == null)
		{
			throw new FileNotFoundException("No XACML Request file found at location: 'classpath:" + requestFileLocation + "'");
		}

		LOGGER.debug("Request file to read: {}", requestFileURL);
		Unmarshaller u = XACMLBindingUtils.createXacml3Unmarshaller();
		Request request = (Request) u.unmarshal(requestFileURL);
		return request;
	}
	
	public static String printRequest(Request request)
	{
		StringWriter writer = new StringWriter();
		try
		{
			Marshaller marshaller = XACMLBindingUtils.createXacml3Marshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(request, writer);
		} catch (Exception e)
		{
			LOGGER.error("Error marshalling Request", e);
		}

		return writer.toString();
	}
	
	/**
	 * This creates the XACML response from file on classpath
	 * 
	 * @param responseFileLocation
	 *            path to the response file (with Spring-supported URL prefixes: 'classpath:', etc.)
	 * @return the XML/JAXB Response or null if any error
	 * @throws JAXBException
	 *             error reading XACML 3.0 Request from the file at {@code responseFileLocation}
	 * @throws FileNotFoundException
	 *             no file found at {@code responseFileLocation}
	 */
	public static Response createResponse(String responseFileLocation) throws JAXBException, FileNotFoundException
	{
		/**
		 * Get absolute path/URL to response file in a portable way, using current class loader. As
		 * per javadoc, the name of the resource passed to ClassLoader.getResource() is a
		 * '/'-separated path name that identifies the resource. So let's build it. Note: do not use
		 * File.separator as path separator, as it will be turned into backslash "\\" on Windows,
		 * and will be URL-encoded (%5c) by the getResource() method (not considered path separator
		 * by this method), and file will not be found as a result.
		 */
		URL responseFileURL = ResourceUtils.getURL(responseFileLocation);
		LOGGER.debug("Response file to read: {}", responseFileURL);
		Unmarshaller u = XACMLBindingUtils.createXacml3Unmarshaller();
		Response response = (Response) u.unmarshal(responseFileURL);
		LOGGER.debug("XACML Response: {}", Utils.printResponse(response));
		return response;
	}

	public static String printResponse(Response response)
	{
		StringWriter writer = new StringWriter();
		try
		{
			Marshaller marshaller = XACMLBindingUtils.createXacml3Marshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(response, writer);
		} catch (Exception e)
		{
			LOGGER.error("Error marshalling Response", e);
		}

		return writer.toString();
	}
}
