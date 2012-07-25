package com.thalesgroup.pdp.core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import com.sun.xacml.Obligation;
import com.sun.xacml.ctx.StatusDetail;

import oasis.names.tc.xacml._2_0.context.schema.os.ActionType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeType;
import oasis.names.tc.xacml._2_0.context.schema.os.AttributeValueType;
import oasis.names.tc.xacml._2_0.context.schema.os.EnvironmentType;
import oasis.names.tc.xacml._2_0.context.schema.os.RequestType;
import oasis.names.tc.xacml._2_0.context.schema.os.ResourceType;
import oasis.names.tc.xacml._2_0.context.schema.os.SubjectType;

/**
 * 
 * @author Romain FERRARI, romain.ferrari[AT]thalesgroup.com
 * This Class is about XACML Processing tools to make 
 * the developers life easier
 *
 */
public final class XacmlProcessing {
	
	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(XacmlProcessing.class);
	
	static final String STRING_XS_DATATYPE = "http://www.w3.org/2001/XMLSchema#string";
	static final String SUBJECT_ID_XACML_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
    static final String RESOURCE_ID_XACML_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
    static final  String ACTION_ID_XACML_ATTRIBUTE_ID = "urn:oasis:names:tc:xacml:1.0:action:action-id";
    static final String LEVEL_OF_AUTHN_PERMIS_ATTRIBUTE_ID = "urn:oid:1.2.826.0.1.3344810.1.1.104";
	
    private XacmlProcessing() {    	
    }
    
	/**
	 * XACML Request creation based on 3 variables : userID (ex: rferrari), resourceId (ex: TRTP2105), actionId (ex: sudo)
	 * @param userid 
	 * @param resourceid
	 * @param actionid
	 * @return RequestType
	 */
	public static RequestType createRequest(String userid, String resourceid, String actionid)
	{
		LOGGER.debug("Subject: " + userid);
		LOGGER.debug("Resource: " + resourceid);
		LOGGER.debug("Action: " + actionid);

	    LOGGER.debug("Create XML Response");
		ArrayList<AttributeType> subjectAttributes = new ArrayList<AttributeType>(); 
        
		LOGGER.debug("Forging subject...");
        AttributeType subjAttr = new AttributeType();
        subjAttr.setAttributeId(SUBJECT_ID_XACML_ATTRIBUTE_ID);
        subjAttr.setDataType(STRING_XS_DATATYPE);
        AttributeValueType subjAttrVal = new AttributeValueType();
        subjAttrVal.getContent().add(userid);
        subjAttr.getAttributeValue().add(subjAttrVal );
        
        subjectAttributes.add(subjAttr);
        
        // resource
        LOGGER.debug("Forging resource...");
        ArrayList<AttributeType> resAttributes = new ArrayList<AttributeType>(); 
        
        AttributeType resAttr = new AttributeType();
        resAttr.setAttributeId(RESOURCE_ID_XACML_ATTRIBUTE_ID);
        resAttr.setDataType(STRING_XS_DATATYPE);
        AttributeValueType resAttrVal = new AttributeValueType();
        resAttrVal.getContent().add(resourceid);
        resAttr.getAttributeValue().add(resAttrVal);
        
        resAttributes.add(resAttr);
        
        // action
        LOGGER.debug("Forging action...");
        ArrayList<AttributeType> actionAttributes = new ArrayList<AttributeType>(); 
        
        AttributeType actionAttr = new AttributeType();
        actionAttr.setAttributeId(ACTION_ID_XACML_ATTRIBUTE_ID);
        actionAttr.setDataType(STRING_XS_DATATYPE);
        AttributeValueType actionAttrVal = new AttributeValueType();
        actionAttrVal.getContent().add(actionid);
        actionAttr.getAttributeValue().add(actionAttrVal);
        
        actionAttributes.add(actionAttr);
        
        // environment
        LOGGER.debug("Forging environment...");
        ArrayList<AttributeType> envAttributes = new ArrayList<AttributeType>();
        //...

        RequestType xacmlRequest = new RequestType();
        
        SubjectType xacmlSubject = new SubjectType();
        xacmlSubject.getAttribute().addAll(subjectAttributes);
        
        ResourceType xacmlResource = new ResourceType();
        xacmlResource.getAttribute().addAll(resAttributes);
        
        ActionType xacmlAction = new ActionType();
        xacmlAction.getAttribute().addAll(actionAttributes);

        EnvironmentType xacmlEnvironment = new EnvironmentType();
        xacmlEnvironment.getAttribute().addAll(envAttributes);
        
        LOGGER.debug("Assembling XACML...");
        xacmlRequest.getSubject().add(xacmlSubject);
        xacmlRequest.getResource().add(xacmlResource);
        xacmlRequest.setAction(xacmlAction);
        xacmlRequest.setEnvironment(xacmlEnvironment);

		return xacmlRequest;	
	}
	
	/**
	 * 
	 * TODO: Decrease NCSS
	 * 
	 * @param myResponse :<br />
	 * 			The response from the evaluation (PERMIT, DENY, NOT_APPLICABLE, INDETERMINATE) 
	 * @param myStatusCode :<br />
	 * 			The status code from the response provided by the evaluation (urn:oasis:names:tc:xacml:1.0:status:ok)
	 * @param myStatusDetail :<br />
	 * 			The message when something goes wrong explaining the status code
	 * @param myObligations :<br />
	 * @param myNamespace :<br />
	 * 			The namespace of the XACML response (urn:oasis:names:tc:xacml:2.0:context:schema:os)
	 * @return Document (org.jdom.Document)
	 */
	public static Document forgeXml(String myResponse, String myStatusCode, StatusDetail myStatusDetail, Set myObligations, String myNamespace)
	{
		Namespace namespace = Namespace.getNamespace(myNamespace);		
		Element root = new Element("Response", namespace);	
		Namespace xsi = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
		root.addNamespaceDeclaration(xsi);
		root.setAttribute("schemaLocation", myNamespace +" access_control-xacml-2.0-context-schema-os.xsd", xsi);
		Document document = new Document(root);
		
		Namespace attributeNamespace = Namespace.getNamespace(myNamespace);
		
		/*
		 * Element Result
		 */
		Element result = new Element("Result", attributeNamespace);
		root.addContent(result);

		/*
		 * Element Decision
		 */
		Element decision = new Element("Decision", attributeNamespace);
		decision.setText(myResponse);
		result.addContent(decision);
		
		/*
		 * Element Status
		 */
		Element status = new Element("Status", attributeNamespace);
		Element statusCode = new Element("StatusCode", attributeNamespace);
		Attribute code = new Attribute("Value", myStatusCode);
		statusCode.setAttribute(code);
		status.addContent(statusCode);
		if (!(myStatusDetail == null))
		{
			Element statusMsg = new Element("StatusMessage", attributeNamespace);
			statusMsg.setText(myStatusDetail.toString());
			status.addContent(statusMsg);
		}		
		result.addContent(status);
		
		/*
		 * Element Obligations
		 */
		if (!myObligations.isEmpty())
		{
			Element obligations = new Element("Obligations", attributeNamespace);
			Iterator it = myObligations.iterator();
			while (it.hasNext()) 
			{
				/**
				 * Handling ObligationId 
				 */
				Obligation obligationType = (Obligation) it.next();
				Element obl = new Element("Obligation", attributeNamespace);
				String oblId = obligationType.getId().toString();
				int oblFulFillOn = obligationType.getFulfillOn();
				String oblFulFillOnStr;
				switch (oblFulFillOn)
				{
					case 0:
						oblFulFillOnStr = "PERMIT";
						break;
					case 1:
						oblFulFillOnStr = "DENY";
						break;
					case 2:
						oblFulFillOnStr = "INDETERMINATE";
						break;
					case 3:
						oblFulFillOnStr = "NOT_APPLICABLE";
						break;
					default:
						throw new IllegalStateException("Unknown code");
							
				}
				Attribute oblAttr = new Attribute("ObligationId", oblId);
				Attribute oblFulFillOnAttr = new Attribute("FulFillOn", oblFulFillOnStr);
				obl.setAttribute(oblFulFillOnAttr);
				obl.setAttribute(oblAttr);				
				
				/**
				 * Handling AttributeAssignement
				 */
				List<Obligation> assignements = obligationType.getAssignments();				
				Iterator iit = assignements.iterator();
				while (iit.hasNext())
				{
					com.sun.xacml.ctx.Attribute assignementsType = (com.sun.xacml.ctx.Attribute) iit.next();
					Element assignement = new Element("AttributeAssignment", attributeNamespace);					
					Attribute assignementId = new Attribute("AttributeId", assignementsType.getId().toString());					
					Attribute assignementDataType = new Attribute("DataType", assignementsType.getType().toString());
					/**
					 * Setting Attributes and Value
					 */
					assignement.setText(assignementsType.getValue().encode());
					assignement.setAttribute(assignementId);
					assignement.setAttribute(assignementDataType);	
					obl.addContent(assignement);
				}
				/**
				 * Adding all of that into Obligations
				 */
				obligations.addContent(obl);
			}
			result.addContent(obligations);
		}

		return document;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static Map<String, String> parseXml(String request)
	{
		RequestType rqType = null;
		String subject, resource, action;
		HashMap<String, String> attributes = new HashMap<String, String>();
			
		javax.xml.bind.Unmarshaller unMarsh;
		try {
			unMarsh = JAXBContext.newInstance(RequestType.class).createUnmarshaller();
			JAXBElement<RequestType> userElement = unMarsh.unmarshal(new StreamSource(new StringReader(request)), RequestType.class);
			rqType = userElement.getValue();
		} catch (JAXBException e) {
			LOGGER.fatal(e.getMessage());
		}
		
		/*
		 * 
		 * TODO: Find a better way to get attributes. This is too messy
		 * 
		 */
		subject = rqType.getSubject().get(0).getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString();
		action = rqType.getAction().getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString();
		resource = rqType.getResource().get(0).getAttribute().get(0).getAttributeValue().get(0).getContent().get(0).toString();
		
		attributes.put("subject", subject);
		attributes.put("action", action);
		attributes.put("resource", resource);
		
		return attributes;
	}
	
	/**
	 * 
	 * @param request
	 * @return
	 */
	public static RequestType parseXmlRequestType(String request)
	{
		RequestType rqType = null;
		javax.xml.bind.Unmarshaller unMarsh;
		//request = request.substring(new String("<?xml version=\"1.0\" encoding=\"UTF-16\" standalone=\"no\" ?>").length());
		
		try {
			unMarsh = JAXBContext.newInstance(RequestType.class).createUnmarshaller();
			JAXBElement<RequestType> userElement = unMarsh.unmarshal(new StreamSource(new StringReader(request)), RequestType.class);
			rqType = userElement.getValue();
		} catch (JAXBException e) {
			LOGGER.fatal(e.getMessage());
		}
		
		return rqType;
	}
}
