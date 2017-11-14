XACML SDK
=========
[![License badge](https://img.shields.io/badge/license-GPL-blue.svg)](https://opensource.org/licenses/GPL-3.0)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/350fb29478014aec81bd6e28067e1355)](https://www.codacy.com/app/romain-ferrari/xacmlsdk?utm_source=tuleap.ow2.org&amp;utm_medium=referral&amp;utm_content=plugins/git/authzforce/xacmlsdk&amp;utm_campaign=Badge_Grade)

Quick Start
-----------
In the mean time, to use the library, you have to build it from source. 
To do this follow the instructions below.

### Prerequisites
* [Git](https://git-scm.org)
* [Maven](https://maven.apache.org/)

### Setup
Clone this repository, install dependencies with mvn.

```bash
git clone https://github.com/authzforce/xacmlsdk.git && cd xacmlsdk
mvn compile -DskipTests=true -Dmaven.javadoc.skip=true -B -V
```

Install the package within your own maven repository.
```bash
mvn install
````

You can start using it inside your java project by adding the dependency to your pom file
```xml
<dependency>
	<groupId>com.thalesgroup.authzforce</groupId>
	<artifactId>xacml-sdk-core</artifactId>
	<version>5.0.0-SNAPSHOT</version>
</dependency>
````

Development
-----------
For more information on contributing to AuthZForce, see `CONTRIBUTING.md`.

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
