package com.thalesgroup.authzforce.sdk.core.schema.category;

import java.util.ArrayList;
import java.util.List;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;

public class Category extends Attributes {
	
	/**
	 * Add an attribute to the category 
	 * @param attr
	 */
	public void addAttribute(final Attribute attr) {
		List<Attribute> attrs = new ArrayList<Attribute>(this.getAttributes());
		attrs.add(attr);
		
		this.attributes = attrs;
	}
	
	/**
	 * Add an attribute to the category 
	 * @param attr
	 */
	public void deleteAttribute(final Attribute attr) {
		List<Attribute> attrs = new ArrayList<Attribute>(this.getAttributes());
		attrs.remove(attr);
		this.attributes = attrs;
	}
	
}
