package com.thalesgroup.authzforce.sdk.core.schema.category;

import com.thalesgroup.authzforce.xacml._3_0.identifiers.XACMLCategory;

public class ResourceCategory extends Category {
	
	public ResourceCategory() {
		this.category = XACMLCategory.XACML_3_0_RESOURCE_CATEGORY_RESOURCE.value();
	}

}
