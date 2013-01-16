package com.thalesgroup.authzforce.sdk.core.schema;

public final class XACMLAttributeId {
	
	private XACMLAttributeId() {
	}

	/**
	 * Subject
	 */
	public static final String XACML_SUBJECT_DNS_NAME 						= "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name";
	public static final String XACML_SUBJECT_IP_ADDRESS 					= "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address";
	public static final String XACML_SUBJECT_AUTHENTICATION_METHOD			= "urn:oasis:names:tc:xacml:1.0:subject:authentication-method";
	public static final String XACML_SUBJECT_AUTHENTICATION_TIME 			= "urn:oasis:names:tc:xacml:1.0:subject:authentication-time";
	public static final String XACML_SUBJECT_KEY_INFO 						= "urn:oasis:names:tc:xacml:1.0:subject:key-info";
	public static final String XACML_SUBJECT_REQUEST_TIME 					= "urn:oasis:names:tc:xacml:1.0:subject:request-time";
	public static final String XACML_SUBJECT_SESSION_START_TIME 			= "urn:oasis:names:tc:xacml:1.0:subject:session-start-time";
	public static final String XACML_SUBJECT_SUBJECT_ID 					= "urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	public static final String XACML_SUBJECT_SUBJECT_ID_QUALIFIER 			= "urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier";
	public static final String XACML_SUBJECT_SUBJECT_ID_ROLE	 			= "urn:oasis:names:tc:xacml:1.0:subject:subject-id-role";
	
	public static final String XACML_SUBJECT_CATEGORY_ACCESS_SUBJECT 		= "urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
	public static final String XACML_SUBJECT_CATEGORY_CODEBASE		 		= "urn:oasis:names:tc:xacml:1.0:subject-category:codebase";
	public static final String XACML_SUBJECT_CATEGORY_INTERMEDIARY_SUBJECT 	= "urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject";
	public static final String XACML_SUBJECT_CATEGORY_RECIPIENT_SUBJECT 	= "urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject";
	public static final String XACML_SUBJECT_CATEGORY_REQUESTING_MACHINE 	= "urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine";
	
	/**
	 * Resource
	 */
	public static final String XACML_RESOURCE_RESOURCE_ID 					= "urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	public static final String XACML_RESOURCE_RESOURCE_LOCATION 			= "urn:oasis:names:tc:xacml:1.0:resource:resource-location";	
	public static final String XACML_RESOURCE_SIMPLE_FILE_NAME 				= "urn:oasis:names:tc:xacml:1.0:resource:simple-file-name";
	
	/**
	 * Action
	 */
	public static final String XACML_ACTION_ACTION_ID 						= "urn:oasis:names:tc:xacml:1.0:action:action-id";
	public static final String XACML_ACTION_IMPLIED_ACTION 					= "urn:oasis:names:tc:xacml:1.0:action:implied-action";

	/**
	 * Custom
	 */
	public static final String XACML_RESOURCE_RESOURCE_LOCATION_ID 			= "urn:oasis:names:tc:xacml:1.0:resource:resource-location-id";
	public static final String XACML_RESOURCE_RESOURCE_LOCATION_ID_ACTION	= "urn:oasis:names:tc:xacml:1.0:resource:resource-location-id-action";
	
	public static final String XACML_ACTION_ACTION_TYPE						= "urn:oasis:names:tc:xacml:1.0:action:action-type";
	public static final String XACML_ACTION_IMPLIED_ACTION_ID				= "urn:oasis:names:tc:xacml:1.0:action:implied-action-id";
	public static final String XACML_ACTION_IMPLIED_ACTION_ID_NETWORK		= "urn:oasis:names:tc:xacml:1.0:action:implied-action-network";
	public static final String XACML_ACTION_IMPLIED_ACTION_ID_NETWORK_ID	= "urn:oasis:names:tc:xacml:1.0:action:implied-action-network-id";
	
	/**
	 * Tools
	 */
	public static final String[] XACML_ACTION_IMPLIED_ACTION_ID_LIST 		= 	{	XACML_ACTION_IMPLIED_ACTION_ID,
																					XACML_ACTION_IMPLIED_ACTION_ID_NETWORK,
																					XACML_ACTION_IMPLIED_ACTION_ID_NETWORK_ID 
																				};
	public static final String[] XACML_RESOURCE_RESOURCE_LOCATION_LIST 		= 	{	XACML_RESOURCE_RESOURCE_LOCATION,
																					XACML_RESOURCE_RESOURCE_LOCATION_ID,
																					XACML_RESOURCE_RESOURCE_LOCATION_ID_ACTION
																				};
}
