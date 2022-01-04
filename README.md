<!-- markdownlint-disable MD041 -->
<h1 align="center">
  Data Donation Server
</h1>

<p align="center">
    <a href="https://github.com/corona-warn-app/cwa-ppa-server/commits/" title="Last Commit"><img src="https://img.shields.io/github/last-commit/corona-warn-app/cwa-ppa-server?style=flat"></a>
    <a href="https://github.com/corona-warn-app/cwa-ppa-server/issues" title="Open Issues"><img src="https://img.shields.io/github/issues/corona-warn-app/cwa-ppa-server?style=flat"></a>
    <a href="https://circleci.com/gh/corona-warn-app/cwa-ppa-server" title="Build Status"><img src="https://circleci.com/gh/corona-warn-app/cwa-ppa-server.svg?style=shield&circle-token=4ab059989d10709df19eb4b98ab7c121a25e981a"></a>
        <a href="https://sonarcloud.io/dashboard?id=corona-warn-app_cwa-ppa-server" title="Quality Gate"><img src="https://sonarcloud.io/api/project_badges/measure?project=corona-warn-app_cwa-ppa-server&metric=alert_status"></a>
        <a href="https://sonarcloud.io/component_measures?id=corona-warn-app_cwa-ppa-server&metric=Coverage&view=list" title="Coverage"><img src="https://sonarcloud.io/api/project_badges/measure?project=corona-warn-app_cwa-ppa-server&metric=coverage"></a>
    <a href="https://github.com/corona-warn-app/cwa-ppa-server/blob/HEAD/LICENSE" title="License"><img src="https://img.shields.io/badge/License-Apache%202.0-green.svg?style=flat"></a>
    <a href="https://api.reuse.software/info/github.com/corona-warn-app/cwa-ppa-server" title="REUSE Status"><img src="https://api.reuse.software/badge/github.com/corona-warn-app/cwa-ppa-server"></a>
</p>

<p align="center">
  <a href="#development">Development</a> •
  <a href="#service-apis">Service APIs</a> •
  <a href="#spring-profiles">Spring Profiles</a> •
  <a href="#documentation">Documentation</a> •
  <a href="#support-and-feedback">Support</a> •
  <a href="#how-to-contribute">Contribute</a> •
  <a href="#contributors">Contributors</a> •
  <a href="#repositories">Repositories</a>
</p>

The Data Donation Server for receiving and storing users usage data and event driven user surveys.

## Architecture Overview

You can find the architecture overview [here](/docs/ARCHITECTURE.md), which will give you a good
starting point in how the backend services interact with other services, and what purpose they
serve.

## Development

After you've checked out this repository, you can run the application in one of the following ways:

* As a [Docker](https://www.docker.com/)-based deployment on your local machine. You can run either:
  * Single components using the respective Dockerfile or
  * The full backend using the Docker Compose (which is considered the most convenient way)
* As a [Maven](https://maven.apache.org)-based build on your local machine. If you want to develop
  something in a single component, this approach is preferable.

### Docker-Based Deployment

If you want to use Docker-based deployment, you need to install Docker on your local machine. For
more information about downloading and installing Docker, see
the [official Docker documentation](https://docs.docker.com/get-docker/).

#### Running the Full CWA Data Donation Backend Using Docker Compose

For your convenience, a full setup for local development and testing purposes, including the
generation of test data has been prepared
using [Docker Compose](https://docs.docker.com/compose/reference/overview/). To build the backend
services, run ```docker-compose build``` in the repository's root directory. A default configuration
file can be found under ```.env``` in the root folder of the repository. If the endpoints are to be
exposed to the network the default values in this file should be changed before docker-compose is
run.

Once the services are built, you can start the whole backend using ```docker-compose up```.

The docker-compose contains the following services:

Service           | Description | Endpoint and Default Credentials
------------------|-------------|-----------
edus              | The event-driven user survey service                                            | `http://localhost:8103`
ppac              | The privacy-preserving access control                                           | `http://localhost:8104`
retention         | The retention service                                                           | NO ENDPOINT
postgres-ppdd     | A [postgres] database installation                                              | `localhost:8002` <br> `postgres-ppdd:5432` (from containerized pgadmin) <br> Username: postgres <br> Password: postgres
pgadmin-ppdd      | A [pgadmin](https://www.pgadmin.org/) installation for the postgres database    | `http://localhost:8001` <br> Username: admin <br> Password: admin

### Maven-Based Build

If you want to actively develop in one of the services, the Maven-based runtime is most suitable. To
prepare your machine to run the CWA project locally, we recommend that you first ensure that you've
installed the following:

* Minimum JDK Version 11: [OpenJDK](https://openjdk.java.net/)
  / [SapMachine](https://sap.github.io/SapMachine/)
* [Maven 3.6](https://maven.apache.org/)
* [Postgres]
* [Zenko CloudServer]

If you are already running a local Postgres, you need to create a database `cwa` and run the
following setup scripts:

* Create the different CWA roles first by executing [setup-roles.sql](./setup/setup-roles.sql).
* Create local database users for the specific roles by
  running [create-users.sql](./setup/create-users.sql).

#### Configure

After you made sure that the specified dependencies are running, configure them in the respective
configuration files.

* Configure the Postgres connection in
  the [edus config](./services/edus/src/main/resources/application.yaml) and in
  the [ppac config](./services/ppac/src/main/resources/application.yaml)

#### Build

After you've checked out the repository, to build the project, run ```mvn install``` in your base
directory.

#### Run

##### Run via Terminal

Navigate to the service you want to start and run the spring-boot:run target. The configured
Postgres and the configured S3 compliant object storage are used as default. When you start the
submission service, the endpoint is available on your local port 8080.

If you want to start the submission service, for example, you start it as follows:

```bash
  cd services/edus/
  mvn spring-boot:run
```

##### Run via IDE

If you want to run a service in your favorite IDE, there are already maven run configurations
provided for [edus](./.run/edus.run.xml) and [ppac](./.run/ppac.run.xml).

#### Debugging

To enable the `DEBUG` log level, you can run the application using the Spring `debug` profile.

```bash
mvn spring-boot:run -Dspring.profiles.active=debug
```

To be able to set breakpoints (e.g. in IntelliJ), it may be necessary to use
the ```-Dspring-boot.run.fork=false``` parameter.

## Service APIs

The API that is being exposed by the backend services is documented in
an [OpenAPI](https://www.openapis.org/) specification. The specification files are available at the
following locations:

Service                   | OpenAPI Specification
--------------------------|-------------
EDUS Service  | [services/edus/api_v1.json](./services/edus/api_v1.json)
PPAC Service  | [services/ppac/api_v1.json](./services/ppac/api_v1.json)

## Spring Profiles

Will follow soon.

## Documentation

The full documentation for the Corona-Warn-App can be found in
the [cwa-documentation](https://github.com/corona-warn-app/cwa-documentation) repository. The
documentation repository contains technical documents, architecture information, and whitepapers
related to this implementation.

The documentation for cwa-server can be found under the [/docs](./docs) folder.

The JavaDoc documentation for cwa-server is hosted by Github Pages
at [https://corona-warn-app.github.io/cwa-server](https://corona-warn-app.github.io/cwa-server).

## Support and Feedback

The following channels are available for discussions, feedback, and support requests:

| Type                     | Channel                                                |
| ------------------------ | ------------------------------------------------------ |
| **General Discussion**   | <a href="https://github.com/corona-warn-app/cwa-documentation/issues/new/choose" title="General Discussion"><img src="https://img.shields.io/github/issues/corona-warn-app/cwa-documentation/question.svg?style=flat-square"></a> </a>   |
| **Concept Feedback**     | <a href="https://github.com/corona-warn-app/cwa-documentation/issues/new/choose" title="Open Concept Feedback"><img src="https://img.shields.io/github/issues/corona-warn-app/cwa-documentation/architecture.svg?style=flat-square"></a>  |
| **Backend Issue**        | <a href="https://github.com/corona-warn-app/cwa-server/issues/new/choose" title="Open Backend Issue"><img src="https://img.shields.io/github/issues/corona-warn-app/cwa-server?style=flat-square"></a>  |
| **Other Requests**       | <a href="mailto:corona-warn-app.opensource@sap.com" title="Email CWA Team"><img src="https://img.shields.io/badge/email-CWA%20team-green?logo=mail.ru&style=flat-square&logoColor=white"></a>   |

## How to Contribute

Contribution and feedback are encouraged and always welcome. For more information about how to
contribute, the project structure, as well as additional contribution information, see
our [Contribution Guidelines](./CONTRIBUTING.md). By participating in this project, you agree to
abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Contributors

The German government has asked SAP and Deutsche Telekom to develop the Corona-Warn-App for Germany
as open source software. Deutsche Telekom is providing the network and mobile technology and will
operate and run the backend for the app in a safe, scalable and stable manner. SAP is responsible
for the app development, its framework and the underlying platform. Therefore, development teams of
SAP and Deutsche Telekom are contributing to this project. At the same time our commitment to open
source means that we are enabling -in fact encouraging- all interested parties to contribute and
become part of its developer community.

## Repositories

A list of all public repositories from the Corona-Warn-App can be found [here](https://github.com/corona-warn-app/cwa-documentation/blob/master/README.md#repositories).

## Licensing

Copyright (c) 2021-2022 SAP SE or an SAP affiliate company and Corona-Warn-App contributors.

Licensed under the **Apache License, Version 2.0** (the "License"); you may not use this file except in compliance with the License.

You may obtain a copy of the License from [here](./LICENSE).

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the [LICENSE](./LICENSE) for the specific language governing permissions and limitations under the License.

Please see the [detailed licensing information](https://api.reuse.software/info/github.com/corona-warn-app/cwa-ppa-server) via the [REUSE Tool](https://reuse.software/) for more details.

[Postgres]: https://www.postgresql.org/

[HSQLDB]: http://hsqldb.org/

[Zenko CloudServer]: https://github.com/scality/cloudserver
