package com.thalesgroup.authzforce.attributefinder;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.bouncycastle.util.encoders.Hex;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.finder.AttributeFinderModule;

/**
 * 
 * 	0.2	-	Cache Implementation<br>
 * 		-	Multiple Datatype Support<br>
 * 			<br>
 *	0.1	-	Class implementing an Attribute finder for LDAP.<br>
 * 
 * @author romain.guignard[AT]thalesgroup.com
 * @version 0.2
 */
public class LdapAttributeFinder extends AttributeFinderModule implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8034856518994418335L;

	private static final org.apache.log4j.Logger log4jLogger = org.apache.log4j.Logger
			.getLogger(LdapAttributeFinder.class);

	private String username;
	private String password;
	private String baseDn;
	private String url;
	private String substituteValue;
	private String ldapAttribute;
	private String attributeSupportedId;
	
	private URI subjectCategoryUri = null;
	private URI subjectId = null;
	private URI subjectType = null;
	private StringAttribute subjectVal;
	private String sep = ":";
	private String attributeFinderType;

	private Set set;
	
	/**
	 * Cache configuration
	 * @author romain.ferrari[AT]thalesgroup.com
	 */
	private boolean activate = false;
	private int maxElementsInMemory;
	private boolean overflowToDisk = false;
	private boolean eternal = false;
	private int timeToLiveSeconds;
	private int timeToIdleSeconds;
//	private static CacheManager cacheManager = MyCacheManager.getInstance();
	private static CacheManager cacheManager ;
	private Cache cache;
	private Cache memoryOnlyCache;

	public LdapAttributeFinder() {
	}

	public LdapAttributeFinder(List args) {
		Iterator it = args.iterator();
		while (it.hasNext()) {
			Object val = it.next();
			if (val instanceof HashMap) {
				HashMap map = (HashMap) val;
				if (map.containsKey("url")) {
					url = (String) map.get("url");
				} else if (map.containsKey("username")) {
					username = (String) map.get("username");
				} else if (map.containsKey("password")) {
					password = (String) map.get("password");
				} else if (map.containsKey("baseDn")) {
					baseDn = (String) map.get("baseDn");
				} else if (map.containsKey("attributeSupportedId")) {
					attributeSupportedId = (String) map
							.get("attributeSupportedId");
				} else if (map.containsKey("ldapAttribute")) {
					ldapAttribute = (String) map.get("ldapAttribute");
				} else if (map.containsKey("substituteValue")) {
					substituteValue = (String) map.get("substituteValue");
				}
				/**
				 * Cache configuration
				 * @author romain.ferrari[AT]thalesgroup.com
				 */
				else if (map.containsKey("activate")) {
					activate = new Boolean(Boolean.parseBoolean((String)map.get("activate")));
				} else if (map.containsKey("maxElementsInMemory")) {
					maxElementsInMemory = (int) Integer.parseInt((String) map.get("maxElementsInMemory"));
				} else if (map.containsKey("overflowToDisk")) {
					overflowToDisk = new Boolean(Boolean.parseBoolean((String)map.get("overflowToDisk")));
				} else if (map.containsKey("eternal")) {
					eternal = new Boolean(Boolean.parseBoolean((String)map.get("eternal")));
				} else if (map.containsKey("timeToLiveSeconds")) {
					timeToLiveSeconds = (int) Integer.parseInt((String) map.get("timeToLiveSeconds"));
				} else if (map.containsKey("timeToIdleSeconds")) {
					timeToIdleSeconds = (int) Integer.parseInt((String) map.get("timeToIdleSeconds"));
				}
			}
		}
		log4jLogger.debug("Initialisation of the Ldap Attribute finder");
		log4jLogger.debug("URL = " + url);
		log4jLogger.debug("username = " + username);
		log4jLogger.debug("baseDn = " + baseDn);
		log4jLogger.debug("password = " + password);
		log4jLogger.debug("attributeSupportedId = " + attributeSupportedId);
		log4jLogger.debug("ldapAttribute = " + ldapAttribute);
		log4jLogger.debug("substituteValue = " + substituteValue);
		
		/**
		 * Cache stuff
		 * @author romain.ferrari[AT]thalesgroup.com
		 */
		log4jLogger.debug("Cache activated: " + activate);
		log4jLogger.debug("maxElementsInMemory = " + maxElementsInMemory);
		log4jLogger.debug("overflowToDisk: " + overflowToDisk);
		log4jLogger.debug("eternal: " + eternal);
		log4jLogger.debug("timeToLiveSeconds = " + timeToLiveSeconds);
		log4jLogger.debug("timeToIdleSeconds = " + timeToIdleSeconds);
		
		/**
		 * Cache initialization
		 * 
		 * @author romain.ferrari[AT]thalesgroup.com
		 */
		if (activate) {
			cacheManager = CacheManager.getInstance();
//			cacheManager = CacheManager.create();
			initCache();
		}
	}
	
	/**
	 * Cache initialization
	 * 
	 * @author romain.ferrari[AT]thalesgroup.com	
     * @param name                the name of the cache. Note that "default" is a reserved name for the defaultCache.
     * @param maxElementsInMemory the maximum number of elements in memory, before they are evicted
     * @param overflowToDisk      whether to use the disk store
     * @param eternal             whether the elements in the cache are eternal, i.e. never expire
     * @param timeToLiveSeconds   the default amount of time to live for an element from its creation date
     * @param timeToIdleSeconds   the default amount of time to live for an element from its last accessed or modified date
     */
	private void initCache() {
	      //NOTE: Create a Cache and add it to the static CacheManager, then use it.	      
			memoryOnlyCache = new Cache(this.getClass().getName(), maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
	        cacheManager.addCache(memoryOnlyCache);
	        cache = cacheManager.getCache(this.getClass().getName());
		}
	
	/**
	 * Used to invalidate the cache of this attribute finder
	 * 
	 * @author romain.ferrari[AT]thalesgroup.com
	 */
	public void invalidateCache() {		
		if (cache != null && cache.getSize() > 0) {
			log4jLogger.debug("Invalidating cache");
			this.cache.removeAll();
		}
	}

	// always return true, since this is a feature we always support
	public boolean isDesignatorSupported() {
		return true;
	}

	// return a single identifier that shows support for environment attrs
	public Set getSupportedDesignatorTypes() {
		Set set = new HashSet();
		set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));
		return set;
	}

	/**
	 * 
	 * Cache implementation. Not famous yet but it's a start, TBC
	 * 
	 * @param attributeType
	 *            Attribute's definition (ex:
	 *            urn:oasis:names:tc:xacml:1.0:resource:resource-id)
	 * @param key
	 * @param key2
	 * @param value
	 *            The value to be checked
	 * 
	 * @return null if nothing is found, an EvaluationResult if something is
	 *         found in the Cache
	 * @author romain.ferrari[AT]thalesgroup.com
	 */
	private EvaluationResult checkCache(URI attributeType, String key, String key2, String value) {

		// get an element from cache by key
		String digest = null;
		digest = encodeString(key+key2);
		
		Element e = cache.get(digest);
		Set<String> result;
		if (e != null) {
			result = ((Set) e.getValue());
			log4jLogger.info("Retrieved attributes from cache " + cache.getName());
			Iterator it = result.iterator();
			while (it.hasNext()) { 
				if (attributeType.toString().equals(StringAttribute.identifier)) {
					log4jLogger.debug("Attribute from cache: "+ ((StringAttribute)(it.next())).encode());
				} else if (attributeType.toString().equals(IntegerAttribute.identifier)) {
					log4jLogger.debug("Attribute from cache: "+ ((IntegerAttribute)(it.next())).encode());
				} else if (attributeType.toString().equals(BooleanAttribute.identifier)) {
					log4jLogger.debug("Attribute from cache: "+ ((BooleanAttribute)(it.next())).encode());
				}
			}
		
			return new EvaluationResult(new BagAttribute(attributeType, result));
		}

		return null;
	}

	/**
	 * Key md5 encodage
	 * 
	 * @param msg
	 *            to be encoded
	 * @return message digest
	 */
	private String encodeString(String msg) {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5","BC");			
			byte[] print = Hex.encode(md.digest(new String(msg).getBytes()));
			digest = new String(print);
		} catch (Exception e) {
			log4jLogger.fatal(e.getLocalizedMessage());
			return null;
		}
		
		return digest;
	}
	
	/**
	 * 
	 * Support of mutiple datatypes (String, Integer, Boolean) based on the
	 * datatype described in the policy
	 * 
	 * @author romain.ferrari[AT]thalesgroup.com
	 * 
	 */
	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			URI issuer, URI subjectCategory, EvaluationCtx context,
			int designatorType) {

		log4jLogger
				.debug("Call of LdapAttributeFinder to resolve attribute");

		try {
			subjectCategoryUri = new URI(
					"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
			subjectId = new URI(
					"urn:oasis:names:tc:xacml:1.0:subject:subject-id");
			subjectType = new URI("http://www.w3.org/2001/XMLSchema#string");
		} catch (URISyntaxException e1) {
			log4jLogger.fatal("An exception occured : " + e1);
		}

		// make sure this is an Subject attribute
		if (designatorType != AttributeDesignator.SUBJECT_TARGET)
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// make sure they're asking for our identifier
		if (!attributeId.toString().equals(attributeSupportedId))
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));

		// make sure they're asking for an String/Integer or Boolean return value
		if (!attributeType.toString().equals(StringAttribute.identifier) &&
				!attributeType.toString().equals(IntegerAttribute.identifier) &&
				!attributeType.toString().equals(BooleanAttribute.identifier)) {
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		}			

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

		/**
		 * Checking cache
		 * 
		 * @author romain.ferrari[AT]thalesgroup.com
		 */
		if (activate) {
			EvaluationResult cacheResult = checkCache(attributeType,
					this.getSubstituteValue(), attributeId.toASCIIString(),
					this.getResourceVal().encode());

			if (cacheResult != null) {
				log4jLogger.debug("Attribute found in cache");

				return cacheResult;
			}
		}
		set = new HashSet();
		InitialDirContext ctx = null;

		// Try to retrieve user AttributeCertificate
		NamingEnumeration results = null;
		try {
			ctx = LdapConnection();
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			//TODO: Find a way to enhance this filter crap
			results = ctx.search(baseDn, "(uid=" + subjectVal.getValue() + ")",
					controls);
			// results = ctx.search(subjectVal.getValue(),
			// "(objectclass=inetorgperson)", controls);

			while (results.hasMore()) {
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				

				if (attributes != null) {
					Attribute attributeLdap = attributes.get(ldapAttribute);
					if (attributeLdap == null) {
						log4jLogger.error("No attribute: " + ldapAttribute
								+ " was found for subject: " + subject);
						return new EvaluationResult(
								BagAttribute.createEmptyBag(attributeType));
					}
					NamingEnumeration<?> attributesValues = attributeLdap
							.getAll();
					while (attributesValues.hasMore()) {
						Object value = attributesValues.next();
						/*
						if (value instanceof String) {
							set.add(new StringAttribute((String) value));
						} else if (value instanceof Integer) {
							set.add(new IntegerAttribute((Integer) value));
						} else if (value instanceof Boolean) {
							try {
								set.add(BooleanAttribute.getInstance((String)value));
							} catch (ParsingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						*/
						if (attributeType.toString().equals(StringAttribute.identifier)) {
							set.add(new StringAttribute((String) value));
						} else if (attributeType.toString().equals(IntegerAttribute.identifier)) {
							try {
								set.add(new IntegerAttribute((Integer) Integer.parseInt((String)value)));
							} catch (NumberFormatException e) {
								log4jLogger.fatal("The value of the attribute finder was resolved but it's not an Integer. Check your policy");
								log4jLogger.fatal(e.getLocalizedMessage());
								return new EvaluationResult(
										BagAttribute.createEmptyBag(attributeType));
							}
						} else if (attributeType.toString().equals(BooleanAttribute.identifier)) {
							try {
								set.add(BooleanAttribute.getInstance((String)value));
							} catch (ParsingException e) {							
								log4jLogger.fatal(e.getLocalizedMessage());
								return new EvaluationResult(
										BagAttribute.createEmptyBag(attributeType));
							}
						}
					}

				}
			}

			/**
			 * Cache region, not really implemented yet Just testing
			 */
//			try {
//				cacheManager.put("AttributeFinder"
//						+ cacheManager.getElementAttributes().getSize(), this);
//			} catch (CacheException e) {
//				log4jLogger.debug(e.getLocalizedMessage());
//			}
			/**
			 * Updating cache
			 * 
			 * @author romain.ferrari[AT]thalesgroup.com
			 */
			if (activate) {
				cache.put(new Element(encodeString(this.getSubstituteValue()
						+ attributeId.toASCIIString()), set));
			}
			return new EvaluationResult(new BagAttribute(attributeType, set));
		} catch (NamingException e) {
			log4jLogger.fatal("An exception occured " + e);
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		}
	}

	// Method for LDAP Connection
	private InitialDirContext LdapConnection() {
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		// env.put(Context.PROVIDER_URL, url+"/"+baseDN);
		// env.put(Context.INITIAL_CONTEXT_FACTORY, factory );
		env.put(Context.PROVIDER_URL, url);
		if (username != null)
			env.put(Context.SECURITY_PRINCIPAL, username);
		if (password != null)
			env.put(Context.SECURITY_CREDENTIALS, password);
		// env.put("java.naming.ldap.attributes.binary", ldapAttribute);

		try {
			InitialDirContext ctx = new InitialDirContext(env);
			return ctx;
		} catch (NamingException e) {
			log4jLogger.fatal("An exception occured: " + e);
			throw new RuntimeException(e);
		}
	}
}
