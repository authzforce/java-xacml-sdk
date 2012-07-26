package com.sun.xacml;

import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author najmi
 */
public class BindingUtility {
	
	private static DocumentBuilder db = null;
	
	static DocumentBuilderFactory dbf = null;
	
    static JAXBContext s_jaxbContext = null;
    public static oasis.names.tc.xacml._2_0.context.schema.os.ObjectFactory contextFac =
            new oasis.names.tc.xacml._2_0.context.schema.os.ObjectFactory();
    public static oasis.names.tc.xacml._2_0.policy.schema.os.ObjectFactory policyFac =
            new oasis.names.tc.xacml._2_0.policy.schema.os.ObjectFactory();
    

    public static String jaxbContextPath = "oasis.names.tc.xacml._2_0.context.schema.os:oasis.names.tc.xacml._2_0.policy.schema.os";

    private static final Logger logger = Logger.getLogger(BindingUtility.class.getName());

    public static JAXBContext getJAXBContext() throws JAXBException {
        if (s_jaxbContext == null) {
            s_jaxbContext = JAXBContext.newInstance(
                    jaxbContextPath,
                    BindingUtility.class.getClassLoader());            
        }

        return s_jaxbContext;
    }
    
    public static Unmarshaller getUnmarshaller() throws JAXBException {
    	
        Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
        unmarshaller.setEventHandler(new ValidationEventHandler() {

            public boolean handleEvent(ValidationEvent event) {
                boolean keepOn = false;
                return keepOn;
            }
        });

        return unmarshaller;
    }

    public static Marshaller createMarshaller() {
        try {
            Marshaller marshaller = getJAXBContext().createMarshaller();
            return marshaller;
        } catch (JAXBException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static DocumentBuilder getDocumentBuilder(){  
    	
    	if (dbf == null) { 
		    	dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(false);
				dbf.setIgnoringComments(true);
				dbf.setValidating(false);
			    try {
			        dbf.setFeature("http://xml.org/sax/features/namespaces", false);
			        dbf.setFeature("http://xml.org/sax/features/validation", false); 
			        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false); 
			        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
			        db = dbf.newDocumentBuilder();
				} catch (ParserConfigurationException e1) {
					logger.severe("An exception occured: "+e1);
					return null;
				} 
    		}
         return db;
    }
    
}
