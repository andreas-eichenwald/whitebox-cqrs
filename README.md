# CQRS+ES Banking App

Recruitment task for Java Engineer position @ Whitebox.

**Author:** Jędrzej Dąbrowa (@jdabrowa)

It is a Java (version 17) application built by Gradle, with Spring Boot as application framework
and Apache Axon as CQRS/ES framework (backed by dockerized Axon Server in test).

# Requirements

* `docker-compose`
* JDK 17
* `gradle`

# Build instructions

Following command triggers build and runs unit and integration tests with Axon server created by `docker-compose`:

```bash
$ ./gradlew test
```

# Architecture
## Modules

Application consists of top-level Gradle module that does not contain code and hosts 4 submodules:

* `banking-api` - Shared POJOs: Events, Queries, Commands
* `banking-app` - Integration module including others as dependencies, Spring context, local E2E tests
* `banking-command` - Command part of CQRS: Aggregates and command handlers
* `banking-query` - Projectors, Query handlers, materialized views

## `banking-app`

`banking-app` module is application entrypoint - it is a Spring Boot application with `main` method.
It depends on all other modules to provide its functionality.

It also contains a test suite that bootstraps Spring context (`@SpringBootTest`) and that is backed
by [Avast Docker Compose plugin](https://github.com/avast/gradle-docker-compose-plugin) to run
fresh instance of Axon Server during integration tests.
Axon server is exposed using ephemeral port (that is injected to the application during tests via Env variable
in `banking-app/build.gradle`)
to avoid conflicts with locally running servers or open tunnels.

# Dependency management

Where possible dependencies are managed by high-level Gradle components (such as Spring Boot Dependency 
management plugin, Axon BOM and Spock BOM). This way versions are managed centrally and submodules should 
only provide group and artifact ID.

# Configuration

Application configuration is managed by [Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config).

Also Axon Framework is integrated using [Axon Spring Boot Integration](https://docs.axoniq.io/reference-guide/axon-framework/spring-boot-integration),
so it is too configured using `application.yml` file.
