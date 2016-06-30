package com.thalesgroup.authzforce.sdk.core.schema.category;

import org.ow2.authzforce.xacml.identifiers.XACMLCategory;

public class SubjectCategory extends Category {
	
	public SubjectCategory() {
		this.category = XACMLCategory.XACML_1_0_SUBJECT_CATEGORY_ACCESS_SUBJECT.value();
	}

}