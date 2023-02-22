# 2023-02-15 Use Apache Axon framework

Lorem ipsum

# 2023-02-18 Use transactional materialized view of taken account numbers

## Problem

`CreateAccountCommand` should only ever succeed if given account number is not yet used.
There are multiple approaches to solving this issue: client-side validations, using sagas to resolve double-allocated 
numbers or using command-side store of allocated numbers.

https://stackoverflow.com/questions/31386244/cqrs-event-sourcing-check-username-is-unique-or-not-from-eventstore-while-sendin
http://cqrs.nu/Faq

## Chosen Solution

Eventual consistency is not enough for the matter as critical as account number uniqueness. 
Potential business and financial consequences of double allocating are dire. 
For this reason, transactional storage on the command handler will be used to list taken account numbers
and `CreateAccountCommand` will only succeed if successful lock was acquired on the account number. 


# 2023-02-19 Central static account number generator

## Problem

Initial design introduced external component for handling `CreateAccountCommand` - `AccountFactory`.
This was an object orchestrating account number generation and re-emitting `AccountCreatedEvent`.
It was nicely composable, allowing injection of different strategies for account number generation
and has testable event handler that did not require Axon server running (see parent commit for this one).

However, Axon did not seem to like it: it requires Aggregate lifecycle events to only be emitted
from inside the Aggregate itself, not from external component. This design caused 
`java.lang.IllegalStateException: Cannot request current Scope if none is active` being thrown.

The recommended way is to handle creation commands in the constructor of the aggregate.
However, this does not allow for injecting additional strategies or composable behavior.
This seems to be either framework limitation or the issue with my limited current knowledge of the framework,
or even CQRS/ES approach itself.
In order to be able to progress with the task, I am introducing ugly hack as a quick solution:
piece of static singleton that can be supplied at runtime and modified in tests. This should
allow to inject number generation strategy until more canonical and valid solution is identified.

Link: https://discuss.axoniq.io/t/cannot-request-current-scope-if-none-is-active/1683/4
