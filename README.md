XACML SDK
=========
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/1e79b358b0df477c9775624035698db5)](https://www.codacy.com/manual/romain-ferrari/java-xacml-sdk?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=authzforce/java-xacml-sdk&amp;utm_campaign=Badge_Grade)

Includes both the [Policy Decision Point (PDP)](https://authzforce-ce-fiware.readthedocs.io/en/latest/UserAndProgrammersGuide.html#policy-decision-api) and the [Policy Administration Point (PAP)](https://authzforce-ce-fiware.readthedocs.io/en/latest/UserAndProgrammersGuide.html#policy-administration-api) client SDKs

Quick Start
-----------
This artifact is regularly released to maven central. Just add it to your dependencies in your pom file.
You can use the examples from authzforce-xacmlsdk-samples directory to start with.

Build it from sources
-----------
If you want to build it from sources please follow these instructions
### Prerequisites
* [Git](https://git-scm.org)
* [Maven](https://maven.apache.org/)

### Setup
Clone this repository, install dependencies with mvn.

```bash
git clone https://github.com/authzforce/java-xacml-sdk.git && cd java-xacml-sdk
mvn compile 
```

Install the package within your own maven repository.
```bash
mvn install
````

User Guide
-----------
You can start using it inside your java project by adding the dependency to your pom file
```xml
<dependency>
	<groupId>com.thalesgroup.authzforce</groupId>
	<artifactId>xacml-sdk-pdp-rest-impl</artifactId>
	<version>5.0.0</version>
</dependency>
````
for PDP actions

or

```xml
<dependency>
	<groupId>com.thalesgroup.authzforce</groupId>
	<artifactId>xacml-sdk-pap-rest-impl</artifactId>
	<version>5.0.0</version>
</dependency>
````
for PAP actions

PDP vs PAP
----------

The PDP provides an API for getting authorization decisions computed by a XACML-compliant access control engine. It supports the following actions

* `getAuthz` to get the computed decision

see the [PDP samples](authzforce-xacmlsdk-samples/src/main/java/org/ow2/authzforce/sdk/pdp) for usage examples

The PAP provides API for managing XACML policies to be handled by the Authorization Service PDP. It supports the following actions

* `getDomains` to list all domains in the access control engine
* `getDomain` to get details on a given domain
* `addDomain` to add a new domain
* `deleteDomain` to remove a domain
* `getPolicy` to get details on a policy set
* `addPolicy` to add a policy set to a domain
* `deletePolicy` to delete a policy set from a domain
* `createSimplePolicy` to create (without saving to a domain, i.e. in memory only) a basic policy set based on the root policy. Intended to be used in conjunction with `addPolicy`

see the [PAP samples](authzforce-xacmlsdk-samples/src/main/java/org/ow2/authzforce/sdk/pap) for usage examples

Development
-----------
For more information on contributing to Authzforce, see `CONTRIBUTING.md`.

FAQ
-----------
TODO

License
-------
```
AuthZForce CE-XACML SDK - Software Development Kit for AuthZForce authorization server
Copyright (c) 2016 Thales Services

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses/.
```

References
-----------
* OASIS XACML 3.0: http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-os-en.html
