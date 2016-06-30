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
package org.ow2.authzforce.sdk.core.schema;

import java.util.ArrayList;
import java.util.List;

public class Responses {
	
	private List<Response> responses;	

	public List<Response> getResponses() {
		if(null == responses) {
			responses = new ArrayList<Response>();
		}
		return responses;
	}

	public void setResponses(List<Response> responses) {
		this.responses = responses;
	}
}
