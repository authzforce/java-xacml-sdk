/**
 * Copyright (C) ${h_inceptionYear}-2013 ${h_copyrightOwner} - All rights reserved.
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
package com.thalesgroup.authzforce.sdk;

import java.util.List;

import com.thalesgroup.authzforce.sdk.core.schema.Action;
import com.thalesgroup.authzforce.sdk.core.schema.Environment;
import com.thalesgroup.authzforce.sdk.core.schema.Resource;
import com.thalesgroup.authzforce.sdk.core.schema.Response;
import com.thalesgroup.authzforce.sdk.core.schema.Responses;
import com.thalesgroup.authzforce.sdk.core.schema.Subject;
import com.thalesgroup.authzforce.sdk.exceptions.XacmlSdkException;

public interface XacmlSdk {

	/**
	 * This method is used to create the XML request based on the RequestType
	 * 
	 * @return XML Request (String)
	 */
	public abstract String toString();

	/**
	 * 
	 * @param subject
	 * @param resources
	 * @param actions
	 * @param environment
	 * @return
	 * @throws XacmlSdkException
	 */
	public abstract Responses getAuthZ(Subject subject,
			List<Resource> resources, List<Action> actions,
			Environment environment) throws XacmlSdkException;

	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param action
	 * @param environment
	 * @return
	 * @throws XacmlSdkException
	 */
	public abstract Response getAuthZ(Subject subject, Resource resource,
			Action action, Environment environment) throws XacmlSdkException;

	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param action
	 * @param environment
	 * @return
	 * @throws XacmlSdkException
	 */
	public abstract Responses getAuthZ(Subject subject,
			List<Resource> resource, Action action, Environment environment)
			throws XacmlSdkException;

	/**
	 * 
	 * @param subject
	 * @param resource
	 * @param action
	 * @param environment
	 * @return
	 * @throws XacmlSdkException
	 */
	public abstract Responses getAuthZ(Subject subject, Resource resource,
			List<Action> action, Environment environment)
			throws XacmlSdkException;

}