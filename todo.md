# High-level requirements
- [x] It should be possible to open a bank account with an initial deposit and credit line.
  - Validate:
    - [x] Account number format
    - [x] Deposit and credit line > 0
- [x] It should be possible to retrieve the current account balance of a given bank account.
- [x] It should be possible to test if a pending debit payment would exceed the overdraft limit of that bank account.
- [ ] It should be possible to get a list of all transactions booked of a given bank account since a given calendar date.
- [x] It should be possible to receive a list of all bank accounts in the red, i.e., whose account balance is lower than zero.

# Implementation improvements

- [ ] TODOs
- [ ] Proper money format (`BigDecimal` instead of `long`)

# Project setup improvements
- [ ] Version management in gradle build files
- [ ] Expose non-standard ports configuration in test setup in Gradle and Spring configs to prevent conflicts with standard local setup
- [ ] Create fast test configuration to run only unit tests without docker-compose, Axon server and Spring context
