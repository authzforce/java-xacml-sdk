package org.ow2.authzforce.sdk.core.schema.category;

import org.ow2.authzforce.xacml.identifiers.XACMLCategory;

public class ActionCategory extends Category {

	public ActionCategory() {
		this.category = XACMLCategory.XACML_3_0_ACTION_CATEGORY_ACTION.value();
	}
}
