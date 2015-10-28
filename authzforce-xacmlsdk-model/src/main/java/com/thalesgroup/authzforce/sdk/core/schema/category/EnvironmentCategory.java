package com.thalesgroup.authzforce.sdk.core.schema.category;

import com.thalesgroup.authzforce.xacml._3_0.identifiers.XACMLCategory;

public class EnvironmentCategory extends Category {

	public EnvironmentCategory() {
		this.category = XACMLCategory.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT.value();
	}
}
