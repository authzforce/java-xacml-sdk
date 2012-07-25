package com.thalesgroup.authzforce.attributeFinder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.finder.AttributeFinderModule;

public class AttributeCertificateFinder extends AttributeFinderModule {

	private static final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger.getLogger(AttributeCertificateFinder.class);
	
	private String username;
	private String password ;
	private String baseDN;
	private String url;
	private String substituteValue;
	private String ldapAttribute;
	private String attributeSupportedId;

	private URI subjectCategoryUri = null;
	private URI subjectId = null;
	private URI subjectType = null;
	private StringAttribute subjectVal;
	private String sep =":";

	public AttributeCertificateFinder(){
		
	}	  
	
	public AttributeCertificateFinder(List args){
		Iterator it = args.iterator();
		while (it.hasNext()){
			Object val = it.next();
			if (val instanceof HashMap){
				HashMap map= (HashMap) val;
				if (map.containsKey("url")){
					url = (String) map.get("url");
				}
				else if (map.containsKey("username")){
					username = (String) map.get("username");
				}
				else if (map.containsKey("password")){
					password = (String) map.get("password");
				}
				else if (map.containsKey("baseDn")){
					baseDN = (String) map.get("baseDn");
				}
				else if (map.containsKey("attributeSupportedId")){
					attributeSupportedId = (String) map.get("attributeSupportedId");
				}
				else if (map.containsKey("ldapAttribute")){
					ldapAttribute = (String) map.get("ldapAttribute");
				}
				else if (map.containsKey("substituteValue")){
					substituteValue = (String) map.get("substituteValue");
				}
			}
		}
		log4jLogger.debug("Initialisation of the Attribute Certificate Connector");
		log4jLogger.debug("URL = "+url);
		log4jLogger.debug("username = "+username);
		log4jLogger.debug("baseDN = "+baseDN);
		log4jLogger.debug("password = "+password);
		log4jLogger.debug("attributeSupportedId = "+attributeSupportedId);
		log4jLogger.debug("ldapAttribute = "+ldapAttribute);
		log4jLogger.debug("substituteValue = "+substituteValue);
	}
	
	
	// always return true, since this is a feature we always support
	public boolean isDesignatorSupported() {
		return true;
	}

	// return a single identifier that shows support for environment attrs
	public Set getSupportedDesignatorTypes() {
		Set set = new HashSet();
		set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));
		set.add(new Integer(AttributeDesignator.RESOURCE_TARGET));
		return set;
	}

	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			URI issuer, URI subjectCategory, EvaluationCtx context,
			int designatorType) {

		log4jLogger.debug("Call of AttributeCertificateFinder to resolve attribute");

		try {
			subjectCategoryUri = new URI("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
			subjectId = new URI("urn:oasis:names:tc:xacml:1.0:subject:subject-id");
			subjectType = new URI("http://www.w3.org/2001/XMLSchema#string");
		} catch (URISyntaxException e1) {
			log4jLogger.fatal("An exception occured : " + e1);
		}
		Set set = new HashSet();
		InitialDirContext ctx = null;

		// make sure this is an Subject attribute
		if ((designatorType != AttributeDesignator.SUBJECT_TARGET) && 
				(designatorType != AttributeDesignator.RESOURCE_TARGET) )
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// make sure they're asking for our identifier
		if (!attributeId.toString().equals(attributeSupportedId))
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// make sure they're asking for an String return value
		if (!attributeType.toString().equals(StringAttribute.identifier))
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// StandardAttributeFactory factory =
		// StandardAttributeFactory.getFactory();
		// List<AttributeValue> resultValues = new ArrayList<AttributeValue>();

		AttributeValue subject = context.getSubjectAttribute(subjectType,
				subjectId, subjectCategoryUri).getAttributeValue();
		// boolean isbag = subject.isBag();
		if (subject.isBag()) {
			BagAttribute attrBag = (BagAttribute) subject;
			Iterator it = attrBag.getValue().iterator();
			while (it.hasNext()) {
				subjectVal = (StringAttribute) it.next();
			}
			log4jLogger.debug("Search attribute : " + attributeId
					+ " for user : " + subjectVal.getValue());
		}
		// Test subject value
		if (subjectVal.getValue() == null) {
			log4jLogger
					.error("Subject value is Null, Unable to retrieve subject-role");
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		}

		// Try to retrieve user AttributeCertificate
		NamingEnumeration results = null;
		try {
			ctx = LdapConnection();
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			results = ctx.search(baseDN, "(uid=" + subjectVal.getValue() + ")",
					controls);
			// results = ctx.search(subjectVal.getValue(),
			// "(objectclass=inetorgperson)", controls);

			while (results.hasMore()) {
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				;
				if (attributes != null) {
					Attribute ACAttribute = attributes.get(ldapAttribute);

					if (ACAttribute == null) {
						log4jLogger.error("Subject: "+ subject+ " not have AttributeCertificate Role in LDAP");
						return new EvaluationResult(
								BagAttribute.createEmptyBag(attributeType));
					}
					log4jLogger.info("Found certificate and size is "+ ACAttribute.size());
					for (int i = 0; i < ACAttribute.size(); i++) {
						if (ACAttribute != null) {
							byte[] encodedAC = null;
							encodedAC = (byte[]) ACAttribute.get(i);
							X509AttributeCertificateHolder attr = null;
							attr = new X509AttributeCertificateHolder(encodedAC);

							// Check validity period of AttributeCertificate
							// Date date = context.getCurrentDate().getValue();
							Date current = new Date(new GregorianCalendar().getTime().getTime());
							Boolean valid = attr.isValidOn(current);
							if (valid == true) {

							} else {

							}

							// Check attributeCertificate signature
							// TODO

							// Retrieve Role Value
							String roleVal = null;
							org.bouncycastle.asn1.x509.Attribute[] ACAttributes = attr.getAttributes();
							for (int j = 0; j < ACAttributes.length; j++) {
								org.bouncycastle.asn1.x509.Attribute ACattrVal = ACAttributes[j];
								ASN1Encodable[] roles = ACattrVal.getAttributeValues();
								for (int k = 0; k < roles.length; k++) {
									log4jLogger.debug("Number of roles is : "
											+ roles.length);
									log4jLogger.debug("rolevalue is : "
											+ roles[j].toString());
									roleVal = roles[j].toString();
									set.add(new StringAttribute(roleVal));
								}
							}

						}
					}
				}
			}
			return new EvaluationResult(new BagAttribute(attributeType, set));
		} catch (NamingException e) {
			log4jLogger.fatal("An exception occured " + e);
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		} catch (IOException e) {
			log4jLogger.fatal("An exception occured " + e);
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		}
	}

	// Method for LDAP Connection
	private InitialDirContext LdapConnection() {

		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
//		env.put(Context.PROVIDER_URL, url+"/"+baseDN);
		// env.put(Context.INITIAL_CONTEXT_FACTORY, factory );
		env.put(Context.PROVIDER_URL, url);
		if (username != null)
			env.put(Context.SECURITY_PRINCIPAL, username);
		if (password != null)
			env.put(Context.SECURITY_CREDENTIALS, password);
		env.put("java.naming.ldap.attributes.binary", ldapAttribute);

		try {
			InitialDirContext ctx = new InitialDirContext(env);
			return ctx;
		} catch (NamingException e) {
			log4jLogger.fatal("An exception occured: " + e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getAttributeSupportedId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSubstituteValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringAttribute getResourceVal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set getSet() {
		// TODO Auto-generated method stub
		return null;
	}
}
