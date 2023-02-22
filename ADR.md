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