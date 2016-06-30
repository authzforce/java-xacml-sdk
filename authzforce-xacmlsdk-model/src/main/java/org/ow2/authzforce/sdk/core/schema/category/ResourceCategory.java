package org.ow2.authzforce.sdk.core.schema.category;

import org.ow2.authzforce.xacml.identifiers.XACMLCategory;

public class ResourceCategory extends Category {
	
	public ResourceCategory() {
		this.category = XACMLCategory.XACML_3_0_RESOURCE_CATEGORY_RESOURCE.value();
	}

}