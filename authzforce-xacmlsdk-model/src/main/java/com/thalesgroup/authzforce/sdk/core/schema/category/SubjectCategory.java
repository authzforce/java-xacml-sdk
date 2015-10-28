package com.thalesgroup.authzforce.sdk.core.schema.category;

import com.thalesgroup.authzforce.xacml._3_0.identifiers.XACMLCategory;

public class SubjectCategory extends Category {
	
	public SubjectCategory() {
		this.category = XACMLCategory.XACML_1_0_SUBJECT_CATEGORY_ACCESS_SUBJECT.value();
	}

}
