# CQRS+ES Banking App

Recruitment task for Java Engineer position @ Whitebox.

**Author:** Jędrzej Dąbrowa (@jdabrowa)

# Requirements

* `docker-compose`
* JDK 17
* `gradle`

# Build instructions

Following command triggers build and runs integration tests with Axon server created by `docker-compose`:

```bash
$ ./gradlew test
```

# Modules 

* `banking-api` - Shared POJOs: Events, Queries, Commands
* `banking-app` - Integration module including others as dependencies, Spring context, local E2E tests
* `banking-command` - Command part of CQRS: Aggregates and command handlers
* `banking-query` - Projectors, Query handlers, materialized views
