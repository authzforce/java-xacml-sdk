package com.thalesgroup.authzforce.sdk.core.schema;

public enum XACMLAttributeId {

	/**
	 * Subject
	 */
	XACML_SUBJECT_DNS_NAME("urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name"), 
	XACML_SUBJECT_IP_ADDRESS("urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address"), 
	XACML_SUBJECT_AUTHENTICATION_METHOD("urn:oasis:names:tc:xacml:1.0:subject:authentication-method"), 
	XACML_SUBJECT_AUTHENTICATION_TIME("urn:oasis:names:tc:xacml:1.0:subject:authentication-time"), 
	XACML_SUBJECT_KEY_INFO("urn:oasis:names:tc:xacml:1.0:subject:key-info"), 
	XACML_SUBJECT_REQUEST_TIME("urn:oasis:names:tc:xacml:1.0:subject:request-time"), XACML_SUBJECT_SESSION_START_TIME(
			"urn:oasis:names:tc:xacml:1.0:subject:session-start-time"), XACML_SUBJECT_SUBJECT_ID(
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id"), XACML_SUBJECT_SUBJECT_ID_QUALIFIER(
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier"), XACML_SUBJECT_SUBJECT_ID_ROLE(
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id-role"),

	XACML_SUBJECT_CATEGORY_ACCESS_SUBJECT(
			"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"), XACML_SUBJECT_CATEGORY_CODEBASE(
			"urn:oasis:names:tc:xacml:1.0:subject-category:codebase"), XACML_SUBJECT_CATEGORY_INTERMEDIARY_SUBJECT(
			"urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject"), XACML_SUBJECT_CATEGORY_RECIPIENT_SUBJECT(
			"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject"), XACML_SUBJECT_CATEGORY_REQUESTING_MACHINE(
			"urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine"),

	/**
	 * Resource
	 */
	XACML_RESOURCE_RESOURCE_ID(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-id"), XACML_RESOURCE_RESOURCE_LOCATION(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-location"), XACML_RESOURCE_SIMPLE_FILE_NAME(
			"urn:oasis:names:tc:xacml:1.0:resource:simple-file-name"),

	/**
	 * Action
	 */
	XACML_ACTION_ACTION_ID("urn:oasis:names:tc:xacml:1.0:action:action-id"), XACML_ACTION_IMPLIED_ACTION(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action"),
			
	/**
	 * Environment
	 */
	XACML_ENVIRONMENT_ENVIRONMENT_ID("urn:oasis:names:tc:xacml:1.0:environment:environment-id"),

	/**
	 * Custom
	 */
	XACML_RESOURCE_RESOURCE_LOCATION_ID(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-location-id"), XACML_RESOURCE_RESOURCE_LOCATION_ID_ACTION(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-location-id-action"),

	XACML_ACTION_ACTION_TYPE("urn:oasis:names:tc:xacml:1.0:action:action-type"), XACML_ACTION_IMPLIED_ACTION_ID(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-id"), XACML_ACTION_IMPLIED_ACTION_ID_NETWORK(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-network"), XACML_ACTION_IMPLIED_ACTION_ID_NETWORK_ID(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-network-id");

	private final String value;

	XACMLAttributeId(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static XACMLAttributeId fromValue(String v) {
		for (XACMLAttributeId c : XACMLAttributeId.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}
		throw new IllegalArgumentException(v);
	}
}
