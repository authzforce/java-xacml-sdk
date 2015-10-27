package com.thalesgroup.authzforce.sdk.core.schema.category;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;

public class Category extends Attributes {
	
	/**
	 * Add an attribute to the category 
	 * @param attr
	 */
	public void addAttribute(final Attribute attr) {
		this.getAttributes().add(attr);
	}
	
	/**
	 * Add an attribute to the category 
	 * @param attr
	 */
	public void deleteAttribute(final Attribute attr) {
		attributes.remove(attr);
	}
	
}
