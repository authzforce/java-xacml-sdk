/**
 * Copyright (C) 2013-2014 Thales Services - ThereSIS - All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/**
 * 
 */
package org.ow2.authzforce.sdk.core.schema;

import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import oasis.names.tc.xacml._3_0.core.schema.wd_17.Attributes;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.MultiRequests;
import oasis.names.tc.xacml._3_0.core.schema.wd_17.RequestDefaults;

/**
 * @author romain.ferrari[AT]thalesgroup.com
 *
 */
public class Request extends oasis.names.tc.xacml._3_0.core.schema.wd_17.Request {
	
	protected Request() {
		super();
	}
	
	public Request(final RequestDefaults requestDefaults, final List<Attributes> attributes, final MultiRequests multiRequests, final boolean returnPolicyIdList, final boolean combinedDecision) {
		super(requestDefaults, attributes, multiRequests, returnPolicyIdList, combinedDecision);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.thalesgroup.authzforce.sdk.xacml.utils.XacmlSdk#toString()
	 */
	@Override
	public String toString() {
		StringWriter stringRequest = new StringWriter();
		try {
			Marshaller marshaller = JAXBContext.newInstance(oasis.names.tc.xacml._3_0.core.schema.wd_17.Request.class).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			marshaller.marshal(this, stringRequest);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		return stringRequest.toString();
	}

}
