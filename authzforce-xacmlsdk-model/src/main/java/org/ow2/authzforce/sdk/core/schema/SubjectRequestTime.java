/**
 * Copyright (C) 2013-2014 Thales Services - ThereSIS - All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ow2.authzforce.sdk.core.schema;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.AttributeValueType;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public final class SubjectRequestTime extends Attribute {

    private static final XACMLAttributeId DEFDAULT_ATTRIBUTE_ID = XACMLAttributeId.XACML_SUBJECT_REQUEST_TIME;
    private static final String DEFAULT_ISSUER = "xacml-sdk-5.0.0";

    public SubjectRequestTime(final String value) {
        super(Arrays.asList(new AttributeValueType(Arrays.asList((Serializable) value), XACMLDatatypes.XACML_DATATYPE_STRING.value(), null)), DEFDAULT_ATTRIBUTE_ID.value(), DEFAULT_ISSUER, true);
    }


    public SubjectRequestTime(final long value) {
        super(Arrays.asList(new AttributeValueType(Arrays.asList((Serializable) String.valueOf(value)), XACMLDatatypes.XACML_DATATYPE_INTEGER.value(), null)), DEFDAULT_ATTRIBUTE_ID.value(), DEFAULT_ISSUER, true);
    }

    public SubjectRequestTime(final Date date) {
        super(Arrays.asList(new AttributeValueType(Arrays.asList((Serializable) new SimpleDateFormat(DATE_FORMAT).format(date)), XACMLDatatypes.XACML_DATATYPE_DATE.value(), null)), DEFDAULT_ATTRIBUTE_ID.value(), DEFAULT_ISSUER, true);
    }
}