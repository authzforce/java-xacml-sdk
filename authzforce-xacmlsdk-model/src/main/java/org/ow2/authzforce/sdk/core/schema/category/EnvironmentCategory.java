package org.ow2.authzforce.sdk.core.schema.category;

import org.ow2.authzforce.xacml.identifiers.XACMLCategory;

public class EnvironmentCategory extends Category {

	public EnvironmentCategory() {
		this.category = XACMLCategory.XACML_3_0_ENVIRONMENT_CATEGORY_ENVIRONMENT.value();
	}
}