package org.ow2.authzforce.sdk.core.schema.category;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attribute;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;

import java.util.ArrayList;
import java.util.List;

public class Category extends Attributes {
	
	/**
	 * Add an attribute to the category 
	 */
	public void addAttribute(final Attribute attr) {
		List<Attribute> attrs = new ArrayList<Attribute>(this.attributes == null ? super.getAttributes() : this.attributes);
		attrs.add(attr);
		
		this.attributes = attrs;
	}
	
	/**
	 * Add an attribute to the category 
	 */
	public void deleteAttribute(final Attribute attr) {
		List<Attribute> attrs = new ArrayList<Attribute>(this.attributes == null ? super.getAttributes() : this.attributes);
		attrs.remove(attr);
		this.attributes = attrs;
	}

	/**
	 * This bypasses {@link #getAttributes()} logic that checks against a transient list.
	 * This is useful because the normal {@link #getAttributes()} in combination with {@link #deleteAttribute(Attribute)}
	 * or {@link #addAttribute(Attribute)}
	 * would result in an inconsistent state {@code (attributes_RO != attributes)}.
	 *
	 * @return the attributes directly from {@link #attributes} list (i.e. not from {@link #attributes_RO})
	 */
	@Override
	public List<Attribute> getAttributes() {
		return this.attributes;
	}
}
