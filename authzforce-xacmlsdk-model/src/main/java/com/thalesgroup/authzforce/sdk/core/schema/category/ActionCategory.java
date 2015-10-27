package com.thalesgroup.authzforce.sdk.core.schema.category;

import com.thalesgroup.authzforce.xacml._3_0.identifiers.XACMLCategory;

public class ActionCategory extends Category {

	public ActionCategory() {
		this.category = XACMLCategory.XACML_3_0_ACTION_CATEGORY_ACTION.toString();
	}
}
