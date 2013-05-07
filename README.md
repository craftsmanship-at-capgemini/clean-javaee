# IDE configurations

## Eclipse Juno setup
- install JBoss tools: http://marketplace.eclipse.org/content/jboss-tools-juno

## Eclipse min config (Indigo or Juno)
- EGit plugin: http://marketplace.eclipse.org/content/egit-git-team-provider
- Maven WTP plugin (update site) plugin: http://download.jboss.org/jbosstools/updates/m2eclipse-wtp/
Some other maven plugins (m2e, m2e-wtp) can not work well with WTP projects.

## NetBeans setup
Project works out of the box with last NetBeans Java EE.
but be aware of https://netbeans.org/bugzilla/show_bug.cgi?id=178644

## IntelliJ setup
Project works out of the box with last IntelliJ Ultimate Edition (Free 30-day trial).

## Building
Maven3 is needed to build the projects correctly.

# 1. Testing
- simple self-documenting test (OrderRepositoryTest.shouldFindOrderWhenExistsOnlyOne)
- complicated example (OrderRepositoryTest.shouldFindAllCustomerOrdersAndIgnoreOthers)
- given-when-then structure
- test data perparation with builder patterns
- some complicated data with constructor+setters
- some complicated data with DBJunit (optional)
- configuration of object under test: dependencies, configuration-values (OrderSchedulerServiceTest.shouldAssignSingleOrderToKrzysztof)
- let's call functionality under test, private method problematic aspect
- expected vs. actual check
- custom asserts
- selecting state from database (OrderRepositoryTest.shouldDeleteClosedOrders)


# 2. Clean business logic
- business case - spec. of makeScheduleForToday
- big picture of code OrderSchedulerService.makeScheduleForToday (tag: business-1)
- marked spec. elements: select orders, rules if-else implementation, balance assignment
- marked big win point - custom collections
- refactored version (tag: business-2)
- other enhancement techniques: sub methods, variables names
- simplify with technical elements (operators in properties, groovy rules)
- ConfigurationFactory, injection points
- groovy rules, injection of ruleset
- final pimped version (tag: business-3)


# 3. Testability
- dependency types: inherit, instantiate, calls-static-member
- dependency to infrastructure: database, file system, servlet context, remote service, etc.
- JSF Controller (tag: testability-1)
- problem mitigation: method extraction + self mocking
- avoiding transitive dependences with injection of value objects
- JSF Controller + FacesUtils (tag: testability-2)
- avoiding transitive dependences with injection of services covering infrastructure


# 4. Extras
