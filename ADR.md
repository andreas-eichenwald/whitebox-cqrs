# ADRs: Architectural Decision Records

This is a chronologically ordered log of high-impact decisions made during the development of the application in this repository.
Its goal is to document the context, problems and chosen solutions. This knowledge should be used in the future
to reason about those decisions and re-evaluate them in new reality should circumstances change.
It's simplified for the use in this task.

## 2023-02-15 Use Apache Axon framework

Even though task requirements don't explicitly require it, this project will be implemented using
Apache Axon framework as Event Store and message bus. 

Decision criteria:
* Established position in the community (popular solution)
* Low learning curve, mostly due to plenty of learning resources available
* Integration with Spring Boot allows for rapid prototyping

## ~~2023-02-19 Central static account number generator~~ (Reverted 2023-02-20)

### ~~Problem~~

~~Initial design introduced external component for handling `CreateAccountCommand` - `AccountFactory`.~~
~~This was an object orchestrating account number generation and re-emitting `AccountCreatedEvent`.~~
~~It was nicely composable, allowing injection of different strategies for account number generation~~
~~and has testable event handler that did not require Axon server running (see parent commit for this one).~~

~~However, Axon did not seem to like it: it requires Aggregate lifecycle events to only be emitted~~
~~from inside the Aggregate itself, not from external component. This design caused~~
~~`java.lang.IllegalStateException: Cannot request current Scope if none is active` being thrown.~~

~~The recommended way is to handle creation commands in the constructor of the aggregate.~~
~~However, this does not allow for injecting additional strategies or composable behavior.~~
~~This seems to be either framework limitation or the issue with my limited current knowledge of the framework,~~
~~or even CQRS/ES approach itself.~~
~~In order to be able to progress with the task, I am introducing ugly hack as a quick solution:~~
~~piece of static singleton that can be supplied at runtime and modified in tests. This should~~
~~allow to inject number generation strategy until more canonical and valid solution is identified.~~

~~Link: https://discuss.axoniq.io/t/cannot-request-current-scope-if-none-is-active/1683/4~~

## 2023-02-20 Use externally provided account number

To resolve issues described in `2023-02-19 Central static account number generator`, new approach was taken:
account number will now be externally locked and decided. Maybe it should even be an aggregate on it's own?
High level outcome is that `AccountAggregate` now assumes `CreateAccountCommand` to have valid and unique
account number in it. 
