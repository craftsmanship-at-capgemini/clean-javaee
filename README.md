# IDE configurations

## Eclipse setup
- install JBoss tools: http://marketplace.eclipse.org/content/jboss-tools-juno

## NetBeans setup
Project works out of the box with last NetBeans Java EE.

## IntelliJ setup
Project works out of the box with last IntelliJ Ultimate Edition (Free 30-day trial).

## Building without IDE
Maven3 is needed to build the projects correctly.

# Topics covered in example

## 1. Testing
- simple self-documenting test (OrderRepositoryTest.shouldFindOrderWhenExistsOnlyOne)
- complicated example (OrderRepositoryTest.shouldFindAllCustomerOrdersAndIgnoreOthers)
- given-when-then structure
- test data preparation with builder patterns
- configuration of object under test: dependencies, configuration-values (OrderSchedulerServiceTest.shouldAssignSingleOrderToKrzysztof)
- functionality under test, private method problematic aspect
- expected vs. actual check
- custom asserts with FEST-Assert
- checking state in database (OrderRepositoryTest.shouldDeleteClosedOrders)
- parallel testing, multi thread tests execution of tests with real database

## 2. Clean business logic
- self documenting business case implementation
- business case specification as executable test
- comparison to transaction script pattern OrderSchedulerService.makeScheduleForToday (tag: business-1)
- refactoring using extraction of custom collections (tag: business-2)
- other enhancement techniques: private methods, variables names
- simplify code using technical elements (CDI injection of business rules in groovy from property files)
- final pimped version (tag: business-3)

## 3. Testability
- dependency types mitigation: inherit, instantiate, calls-static-member
- dealing with dependency to infrastructure: database, file system, servlet context, remote service, etc.
- JSF Controller testability problems (tag: testability-1)
- JSF Controller + FacesUtils (tag: testability-2)
- avoiding transitive dependences with injection of value objects
- avoiding transitive dependences with injection of services covering infrastructure

