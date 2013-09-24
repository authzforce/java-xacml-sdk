/**
 * Copyright (C) ${h_inceptionYear}-2013 ${h_copyrightOwner} - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.thalesgroup.authzforce.sdk.core.schema;

public enum XACMLAttributeId {

	/*
	 * Subject
	 */
	XACML_SUBJECT_DNS_NAME("urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name"), 
	XACML_SUBJECT_IP_ADDRESS("urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address"), 
	XACML_SUBJECT_AUTHENTICATION_METHOD("urn:oasis:names:tc:xacml:1.0:subject:authentication-method"), 
	XACML_SUBJECT_AUTHENTICATION_TIME("urn:oasis:names:tc:xacml:1.0:subject:authentication-time"), 
	XACML_SUBJECT_KEY_INFO("urn:oasis:names:tc:xacml:1.0:subject:key-info"), 
	XACML_SUBJECT_REQUEST_TIME("urn:oasis:names:tc:xacml:1.0:subject:request-time"), 
	XACML_SUBJECT_SESSION_START_TIME("urn:oasis:names:tc:xacml:1.0:subject:session-start-time"), 
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject:subject-id
	 */
	XACML_SUBJECT_SUBJECT_ID("urn:oasis:names:tc:xacml:1.0:subject:subject-id"), 
	XACML_SUBJECT_SUBJECT_ID_QUALIFIER("urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier"), 
	XACML_SUBJECT_SUBJECT_ID_ROLE("urn:oasis:names:tc:xacml:1.0:subject:subject-id-role"),

	/*
	 * Subject Category
	 */
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject-category:access-subject
	 */
	XACML_1_0_SUBJECT_CATEGORY_SUBJECT("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"),
	
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject-category:codebase
	 */
	XACML_1_0_SUBJECT_CATEGORY_CODEBASE("urn:oasis:names:tc:xacml:1.0:subject-category:codebase"), 
	
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject
	 */
	XACML_1_0_SUBJECT_CATEGORY_INTERMEDIARY_SUBJECT("urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject"), 
	
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject
	 */
	XACML_1_0_SUBJECT_CATEGORY_RECIPIENT_SUBJECT("urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject"), 
	
	/**
	 * urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine
	 */
	XACML_1_0_SUBJECT_CATEGORY_REQUESTING_MACHINE("urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine"),

	/*
	 * Resource
	 */
	XACML_RESOURCE_RESOURCE_ID("urn:oasis:names:tc:xacml:1.0:resource:resource-id"), 
	XACML_RESOURCE_RESOURCE_LOCATION("urn:oasis:names:tc:xacml:1.0:resource:resource-location"), 
	XACML_RESOURCE_SIMPLE_FILE_NAME("urn:oasis:names:tc:xacml:1.0:resource:simple-file-name"),

	/*
	 * Resource Category
	 */
	/**
	 * urn:oasis:names:tc:xacml:3.0:attribute-category:resource
	 */
	XACML_3_0_RESOURCE_CATEGORY_RESOURCE("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
	/*
	 * Action
	 */
	/**
	 * urn:oasis:names:tc:xacml:1.0:action:action-id
	 */
	XACML_ACTION_ACTION_ID("urn:oasis:names:tc:xacml:1.0:action:action-id"), 
	
	XACML_ACTION_IMPLIED_ACTION("urn:oasis:names:tc:xacml:1.0:action:implied-action"),
			
	/*
	 * Action Category
	 */
	XACML_3_0_ACTION_CATEGORY_ACTION("urn:oasis:names:tc:xacml:3.0:attribute-category:action"),
	
	/**
	 * Environment Category
	 * urn:oasis:names:tc:xacml:3.0:attribute-category:environment,
	 */
	XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT("urn:oasis:names:tc:xacml:3.0:attribute-category:environment"),
			
	/*
	 * Environment
	 */
	XACML_1_0_ENVIRONMENT_ENVIRONMENT_ID("urn:oasis:names:tc:xacml:1.0:environment:environment-id"),

	
	/*
	 * XACML 1.0
	 */
	REQUEST_CONTEXT_1_0_IDENTIFIER("urn:oasis:names:tc:xacml:1.0:context"),
	
	/*
	 * XACML 2.0
	 */
	REQUEST_CONTEXT_2_0_IDENTIFIER("urn:oasis:names:tc:xacml:2.0:context:schema:os"),
	
	/*
	 * XACML 3.0
	 */
	REQUEST_CONTEXT_3_0_IDENTIFIER("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"),
	
	/*
	 * Custom
	 */
	XACML_RESOURCE_RESOURCE_LOCATION_ID(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-location-id"), XACML_RESOURCE_RESOURCE_LOCATION_ID_ACTION(
			"urn:oasis:names:tc:xacml:1.0:resource:resource-location-id-action"),

	XACML_ACTION_ACTION_TYPE("urn:oasis:names:tc:xacml:1.0:action:action-type"), XACML_ACTION_IMPLIED_ACTION_ID(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-id"), XACML_ACTION_IMPLIED_ACTION_ID_NETWORK(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-network"), XACML_ACTION_IMPLIED_ACTION_ID_NETWORK_ID(
			"urn:oasis:names:tc:xacml:1.0:action:implied-action-network-id"),
	
	/*
	 * Constants
	 */
	RESOURCE_CATEGORY("urn:oasis:names:tc:xacml:3.0:attribute-category:resource"),
    SUBJECT_CATEGORY("urn:oasis:names:tc:xacml:1.0:subject-category:access-subject"),
    ACTION_CATEGORY("urn:oasis:names:tc:xacml:3.0:attribute-category:action"),    
    ANY ("Any"),

    /**
     * XACML 1.0 identifier
     */
    XACML_1_0_IDENTIFIER("urn:oasis:names:tc:xacml:1.0:policy"),

    /**
     * XACML 2.0 identifier
     */
    XACML_2_0_IDENTIFIER("urn:oasis:names:tc:xacml:2.0:policy:schema:os"),

    /**
     * XACML 2.0 identifier
     */
    XACML_3_0_IDENTIFIER("urn:oasis:names:tc:xacml:3.0:core:schema:wd-17"),

    /**
     * XACML 1.0 identifier
     */
    XACML_VERSION_1_0("0"),

    /**
     * Version identifier for XACML 1.1 (which isn't a formal release so has no namespace string,
     * but still exists as a separate specification)
     */
    XACML_VERSION_1_1("1"),

    /**
     * Version identifier for XACML 1.2
     */
    XACML_VERSION_2_0("2"),

    /**
     * Version identifier for XACML 3.0
     */
    XACML_VERSION_3_0("3"),
    
    /**
     * The standard URI for listing a resource's id
     */
    RESOURCE_ID("urn:oasis:names:tc:xacml:1.0:resource:resource-id"),

    /**
     * The standard URI for listing a resource's scope  in XACML 1.0 
     */
    RESOURCE_SCOPE_1_0("urn:oasis:names:tc:xacml:1.0:resource:scope"),

    /**
     * The standard URI for listing a resource's scope in multiple resource profile
     */
    RESOURCE_SCOPE_2_0("urn:oasis:names:tc:xacml:2.0:resource:scope"),

    /**
     * Resource scope of Immediate (only the given resource)
     */
    SCOPE_IMMEDIATE("0"),

    /**
     * Resource scope of Children (the given resource and its direct children)
     */
    SCOPE_CHILDREN("1"),

    /**
     * Resource scope of Descendants (the given resource and all descendants at any depth or
     * distance)
     */
    SCOPE_DESCENDANTS("2"),

    MULTIPLE_CONTENT_SELECTOR("urn:oasis:names:tc:xacml:3.0:profile:" + "multiple:content-selector"),

    CONTENT_SELECTOR("urn:oasis:names:tc:xacml:3.0:content-selector"),

    ATTRIBUTES_ELEMENT("Attributes"),

    MULTI_REQUESTS("MultiRequests"),

    REQUEST_DEFAULTS("RequestDefaults"),

    ATTRIBUTE_ELEMENT("Attribute"),

    ATTRIBUTES_CATEGORY("Category"),

    ATTRIBUTES_ID("id"),

    RETURN_POLICY_LIST("ReturnPolicyIdList"),

    COMBINE_DECISION("CombinedDecision"),
    
    ATTRIBUTES_CONTENT("Content"),

    RESOURCE_CONTENT("ResourceContent");

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