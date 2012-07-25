package com.thalesgroup.authzforce.attributefinder;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.bouncycastle.util.encoders.Hex;

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
 * 	1.1	-	Cache added to the AttributeFinder using ehcache.<br>
 * 			<br>
 *  1.0 -   Class implementing an Attribute finder for an SQL database.<br>
 *          Arguments needed from the config.xml file :<br>
 *          <url>jdbc:mysql://localhost:3305/</url> <username>root</username><br>
 *          <password></password> <dbName>keystone-rfo</dbName><br>
 *          <driver>com.mysql.jdbc.Driver</driver><br>
 *          <attributeSupportedId>tenantMember</attributeSupportedId><br>
 *          <sqlRequest>SELECT u.name as $alias FROM user_roles ur, tenants t,<br>
 *          users u WHERE ur.tenant_id = t.id AND u.id = ur.user_id AND t.id = 
 *          $filter </sqlRequest><br>
 *          <substituteValue>urn:oasis:names:tc:xacml:1.0:resource<br>
 *          :resource-id</substituteValue><br>
 * 			<br>
 *          While $filter is the variable part used to make a filter in the SQL<br>
 *          query mapped to the AttributeValue in the XACML request defined with<br>
 *          the substituteValue option.<br>
 * 			<br>
 *          $alias is used to make the developper's life easier with the parsing<br>
 *          of the SQL response.<br>
 * 			<br>
 *          The column name where we extract the values is defined by the<br>
 *          attributeSupportedId option.<br>
 *          
 * @author romain.ferrari[AT]thalesgroup.com
 * @version 1.1
 */
public class SqlAttributeFinder extends AttributeFinderModule implements
		Serializable {

	private static final long serialVersionUID = -127825331848762186L;

	private static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger
			.getLogger(SqlAttributeFinder.class);

	private String username;
	private String password;
	private String dbName;
	private String url;
	private String substituteValue;
	private String attributeSupportedId;
	private String sqlRequest;
	private String driver;

	private URI resourceId = null;
	private URI resourceType = null;
	private URI resourceCategoryUri = null;

	private StringAttribute resourceVal;

	private Set set;	

	/**
	 * Cache configuration
	 * 
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
	
	public SqlAttributeFinder() {
	}

	public SqlAttributeFinder(List args) {
		Iterator it = args.iterator();
		while (it.hasNext()) {
			Object val = it.next();
			if (val instanceof HashMap) {
				HashMap map = (HashMap) val;
				if (map.containsKey("url")) {
					url = (String) map.get("url");
				} else if (map.containsKey("dbName")) {
					dbName = (String) map.get("dbName");
				} else if (map.containsKey("driver")) {
					driver = (String) map.get("driver");
				} else if (map.containsKey("sqlRequest")) {
					sqlRequest = (String) map.get("sqlRequest");
				} else if (map.containsKey("username")) {
					username = (String) map.get("username");
				} else if (map.containsKey("password")) {
					password = (String) map.get("password");
				} else if (map.containsKey("attributeSupportedId")) {
					attributeSupportedId = (String) map
							.get("attributeSupportedId");
				} else if (map.containsKey("substituteValue")) {
					substituteValue = (String) map.get("substituteValue");
				}
				/**
				 * Cache configuration
				 * 
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

		LOGGER.debug("Initialisation of the Attribute DB Connector");
		LOGGER.debug("URL = " + url);
		LOGGER.debug("Username = " + username);
		LOGGER.debug("Password = " + password);
		LOGGER.debug("DbName = " + dbName);
		LOGGER.debug("Driver = " + driver);
		LOGGER.debug("AttributeSupportedId = " + attributeSupportedId);
		LOGGER.debug("SqlRequest = " + sqlRequest);
		LOGGER.debug("SubstituteValue = " + substituteValue);

		/**
		 * Cache stuff
		 * @author romain.ferrari[AT]thalesgroup.com
		 */
		LOGGER.debug("Cache activated: " + activate);
		LOGGER.debug("maxElementsInMemory = " + maxElementsInMemory);
		LOGGER.debug("overflowToDisk: " + overflowToDisk);
		LOGGER.debug("eternal: " + eternal);
		LOGGER.debug("timeToLiveSeconds = " + timeToLiveSeconds);
		LOGGER.debug("timeToIdleSeconds = " + timeToIdleSeconds);
		
		/**
		 * Cache initialization 
		 * @author romain.ferrari[AT]thalesgroup.com
		 */
		if (activate) {
			cacheManager = CacheManager.getInstance();
//			cacheManager = CacheManager.create();
			initCache();
		}
	}
	
	private void initCache() {
      //NOTE: Create a Cache and add it to the static CacheManager, then use it.
      /*
       * @param name                the name of the cache. Note that "default" is a reserved name for the defaultCache.
       * @param maxElementsInMemory the maximum number of elements in memory, before they are evicted
       * @param overflowToDisk      whether to use the disk store
       * @param eternal             whether the elements in the cache are eternal, i.e. never expire
       * @param timeToLiveSeconds   the default amount of time to live for an element from its creation date
       * @param timeToIdleSeconds   the default amount of time to live for an element from its last accessed or modified date
       */
		memoryOnlyCache = new Cache(this.getClass().getName(), maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds, timeToIdleSeconds);
        cacheManager.addCache(memoryOnlyCache);
        cache = cacheManager.getCache(this.getClass().getName());
	}
	
	/**
	 * Used to invalidate the cache of this attribute finder
	 * @author romain.ferrari[AT]thalesgroup.com
	 */
	public void invalidateCache() {		
		if (cache != null && cache.getSize() > 0) {
			LOGGER.debug("Invalidating cache");
			this.cache.removeAll();
		}
	}

	// always return true, since this is a feature we always support
	public boolean isDesignatorSupported() {
		return true;
	}

	public String getAttributeSupportedId() {
		return attributeSupportedId;
	}	 

	public Set<?> getSet() {
		return this.set;
	}

	public String getSubstituteValue() {
		return this.substituteValue;
	}

	public StringAttribute getResourceVal() {
		return resourceVal;
	}

	// return a single identifier that shows support for environment attrs
	public Set<Integer> getSupportedDesignatorTypes() {
		Set<Integer> set = new HashSet<Integer>();
		set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));
		set.add(new Integer(AttributeDesignator.RESOURCE_TARGET));

		return set;
	}

	@SuppressWarnings("unchecked")
	/**
	 * 
	 * Cache implementation. Not famous yet but it's a start, TBC
	 * 		 
	 * @param attributeType Attribute's definition (ex: urn:oasis:names:tc:xacml:1.0:resource:resource-id)
	 * @param key
	 * @param key2
	 * @param value The value to be checked
	 * 
	 * @return null if nothing is found, an EvaluationResult if something is found in the Cache
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
			LOGGER.info("Retrieved attributes from cache " + cache.getName());
			Iterator it = result.iterator();
			while (it.hasNext()) { 
				if (attributeType.toString().equals(StringAttribute.identifier)) {
					LOGGER.debug("Attribute from cache: "+ ((StringAttribute)(it.next())).encode());
				} else if (attributeType.toString().equals(IntegerAttribute.identifier)) {
					LOGGER.debug("Attribute from cache: "+ ((IntegerAttribute)(it.next())).encode());
				} else if (attributeType.toString().equals(BooleanAttribute.identifier)) {
					LOGGER.debug("Attribute from cache: "+ ((BooleanAttribute)(it.next())).encode());
				}
			}
		
			return new EvaluationResult(new BagAttribute(attributeType, result));
		}

		return null;
	}

	/**
	 * Key md5 encodage
	 * @param msg to encode
	 * @return message digest
	 */
	private String encodeString(String msg) {
		String digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5","BC");			
			byte[] print = Hex.encode(md.digest(new String(msg).getBytes()));
			digest = new String(print);
		} catch (Exception e) {
			LOGGER.fatal(e.getLocalizedMessage());
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
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EvaluationResult findAttribute(URI attributeType, URI attributeId,
			URI issuer, URI subjectCategory, EvaluationCtx context,
			int designatorType) {

		LOGGER.debug("Call of AttributeDBFinder to resolve attribute "
				+ attributeSupportedId);
		try {
			if (substituteValue == "urn:oasis:names:tc:xacml:1.0:resource:resource-id") {
				resourceCategoryUri = new URI(
						"urn:oasis:names:tc:xacml:1.0:resource-category:access-resource");
			} else {
				resourceCategoryUri = new URI(
						"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject");
			}
			resourceId = new URI(substituteValue);
			resourceType = new URI("http://www.w3.org/2001/XMLSchema#string");
		} catch (URISyntaxException e1) {
			LOGGER.fatal("An exception occured : " + e1);
		}

		// make sure this is an Subject or resource attribute
		if ((designatorType != AttributeDesignator.SUBJECT_TARGET)
				&& (designatorType != AttributeDesignator.RESOURCE_TARGET))
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

		AttributeValue resource = context.getResourceAttribute(resourceType,
				resourceId, null).getAttributeValue();

		if (resource.isBag()) {
			BagAttribute attrBag = (BagAttribute) resource;
			Iterator<StringAttribute> it = attrBag.getValue().iterator();
			while (it.hasNext()) {
				resourceVal = (StringAttribute) it.next();
			}
			LOGGER.debug("Search attribute : " + attributeId
					+ " for resource : " + resourceVal.getValue());
		}
		// Test subject value
		if (resourceVal.getValue() == null) {
			LOGGER.error("Resource value is Null, Unable to retrieve "
					+ attributeId);
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
				LOGGER.debug("Attribute found in cache");

				return cacheResult;
			}
		}
		set = new HashSet();
		Connection con = null;

		try {
			con = DBConnection();
			Statement stmt = con.createStatement();
			ResultSet result;
			String sql = parseSql(sqlRequest, resourceVal.encode(), "filter");
			sql = parseSql(sql, attributeSupportedId, "alias");
			LOGGER.debug("Final SQL request: " + sql);
			result = stmt.executeQuery(sql);
			while (result.next()) {				
//				set.add(new StringAttribute(result.getString(attributeSupportedId)));
				if (attributeType.toString().equals(StringAttribute.identifier)) {
					set.add(new StringAttribute((String) result.getString(attributeSupportedId)));
				} else if (attributeType.toString().equals(IntegerAttribute.identifier)) {
					try {
						set.add(new IntegerAttribute((Integer) Integer.parseInt((String)result.getString(attributeSupportedId))));
					} catch (NumberFormatException e) {
						LOGGER.fatal("The value of the attribute finder was resolved but it's not an Integer. Check your policy");
						LOGGER.fatal(e.getLocalizedMessage());
						return new EvaluationResult(
								BagAttribute.createEmptyBag(attributeType));
					}
				} else if (attributeType.toString().equals(BooleanAttribute.identifier)) {
					try {
						set.add(BooleanAttribute.getInstance((String)result.getString(attributeSupportedId)));
					} catch (ParsingException e) {							
						LOGGER.fatal(e.getLocalizedMessage());
						return new EvaluationResult(
								BagAttribute.createEmptyBag(attributeType));
					}
				}
			}
			/**
			 * Updating cache
			 * 
			 * @author romain.ferrari[AT]thalesgroup.com
			 */
			if (activate) {
				cache.put(new Element(encodeString(this.getSubstituteValue()
						+ attributeId.toASCIIString()), set));
			}
			con.close();
		} catch (SQLException e1) {
			LOGGER.fatal(e1.getLocalizedMessage());
		} catch (InstantiationException e2) {
			LOGGER.fatal(e2.getLocalizedMessage());
		}
		if (set.isEmpty()) {
			return new EvaluationResult(
					BagAttribute.createEmptyBag(attributeType));
		}

		
		
		return new EvaluationResult(new BagAttribute(attributeType, set));
	}

	// Method for DB Connection
	private Connection DBConnection() throws InstantiationException {
		String url = this.url;
		String dbName = this.dbName;
		String driver = this.driver;
		String username = this.username;
		String password = this.password;
		Connection con = null;

		try {
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url + dbName, username, password);
		} catch (InstantiationException e) {
			throw (e);
		} catch (IllegalAccessException e1) {
			LOGGER.fatal(e1.getLocalizedMessage());
		} catch (ClassNotFoundException e2) {
			LOGGER.fatal(e2.getLocalizedMessage());
		} catch (SQLException e3) {
			LOGGER.fatal(e3.getLocalizedMessage());
		}

		return con;
	}

	private String parseSql(String request, String val, String arg) {
		return request.replace("$" + arg, "\"" + val + "\"");
	}
}
