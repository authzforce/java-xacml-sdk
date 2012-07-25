/**
 * 
 */
package com.thalesgroup.test.pdp.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Test;

import com.sun.xacml.Obligation;
import com.sun.xacml.ParsingException;
import com.sun.xacml.ctx.StatusDetail;

/**
 * @author romain.ferrari[AT]thalesgroup.com
 *
 */
public class XacmlProcessingTest extends TestCase{

	/**
	 * Test method for {@link com.thalesgroup.pdp.core.XacmlProcessing#createRequest(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCreateRequest() {
		
		String subject = "subject";
		String resource = "resource";
		String action = "action";
		RequestType request;
		
		request = com.thalesgroup.pdp.core.XacmlProcessing.createRequest(subject, resource, action);
		if (request.getSubject().get(0).getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString() != subject)
			fail("Request badly shaped (subject)");
		else if (request.getResource().get(0).getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString() != resource)
			fail("Request badly shaped (resource)");
		else if (request.getAction().getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString()!= action)
			fail("Request badly shaped (action)");									
	}

	/**
	 * Test method for {@link com.thalesgroup.pdp.core.XacmlProcessing#forgeXml(java.lang.String, java.lang.String, com.sun.xacml.ctx.StatusDetail, java.util.Set, java.lang.String)}.
	 */
	@Test
	public final void testForgeXml() {
		String _response = "PERMIT";
		String _statusCode = "urn:oasis:names:tc:xacml:1.0:status:ok"; 
		StatusDetail _statusDetail = null;
		try {
			_statusDetail = new StatusDetail("");
		} catch (ParsingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<Obligation> _obligations = new HashSet();		
		String _namespace = "urn:oasis:names:tc:xacml:2.0:context:schema:os";
		
		Document doc = com.thalesgroup.pdp.core.XacmlProcessing.forgeXml(_response, _statusCode, _statusDetail, _obligations, _namespace);
		
		List content = doc.getContent();
		Iterator i = content.iterator();
		while(i.hasNext())
		{
			Element val = (Element)i.next();
			if (val.getName() != "Response") {
				fail("Wrong first child: " + val.getName());
			}
			List key = val.getContent();
			Iterator ii = key.iterator();
			while (ii.hasNext())
			{
				Element val2 = (Element)ii.next();
				if (val2.getName() != "Result") {
					fail("Wrong second child: " + val2.getName());
				}
				List key3 = val2.getContent();
				Iterator iii = key3.iterator();
				while (iii.hasNext())
				{
					Element val3 = (Element)iii.next();
					if ((val3.getName() != "Decision") && (val3.getName() != "Status")) {
						fail("Wrong third child: " + val3.getName());
					}
				}
			}						
		}
	}

	/**
	 * Test method for {@link com.thalesgroup.pdp.core.XacmlProcessing#parseXml(java.lang.String)}.
	 */
	@Test
	public final void testParseXml() {
		String subject = "subject";
		String resource = "resource";
		String action = "action";
		String request = "<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"no\" ?> " +
				"<Request xmlns=\"urn:oasis:names:tc:xacml:2.0:context:schema:os\"> " +
				"<Subject><Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" " +
				"DataType=\"http://www.w3.org/2001/XMLSchema#string\"><AttributeValue>"+ subject +"</AttributeValue>" +
				"</Attribute></Subject><Resource><Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:resource:resource-id\" " +
				"DataType=\"http://www.w3.org/2001/XMLSchema#string\"><AttributeValue>" + resource + "</AttributeValue>" +
				"</Attribute></Resource><Action><Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:action:action-id\" " +
				"DataType=\"http://www.w3.org/2001/XMLSchema#string\"><AttributeValue>" + action + "</AttributeValue>" +
				"</Attribute></Action><Environment/></Request>";
		Map<String, String> result = new HashMap<String, String>(); 
		result = com.thalesgroup.pdp.core.XacmlProcessing.parseXml(request);
		
		Iterator<String> i = result.values().iterator();
		Iterator<String> ii = result.keySet().iterator();
		while(i.hasNext() && ii.hasNext())
		{
			String val = (String)i.next();
			String key = (String)ii.next();
			//if (key.toString() == val.toString())
			if ((key.compareToIgnoreCase(val) == 0))
			{
				if ((key.compareToIgnoreCase(subject) != 0) && (key.compareToIgnoreCase(resource) != 0) && (key.compareToIgnoreCase(action) != 0)) {
					fail("Value unknown, accepted values are: "+subject+","+resource+","+action+". Current is:"+val);
				}
			}
			else {
				fail("XML badly shaped (" + key +" != "+ val + ")");
			}
			
		}
		
		
	}

//	/**
//	 * Test method for {@link com.thalesgroup.pdp.impl.XacmlProcessing#parseXmlRequestType(java.lang.String)}.
//	 */
//	@Test
//	public final void testParseXmlRequestType() {
//		fail("Not yet implemented"); // TODO
//	}

}
