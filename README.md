# IDE configurations

## Eclipse Juno setup
- install jboss tools: http://marketplace.eclipse.org/content/jboss-tools-juno

## Eclipse min config (Indigo or Juno)
- EGit plugin: http://marketplace.eclipse.org/content/egit-git-team-provider
- Maven WTP plugin (update site) plugin: http://download.jboss.org/jbosstools/updates/m2eclipse-wtp/
Some other maven plugins (m2e, m2e-wtp) can not work well with WTP projects.

## NetBeans setup
Project works out of the box with last NetBeans Java EE.
but be aware of https://netbeans.org/bugzilla/show_bug.cgi?id=178644

## IntelliJ setup
Project works out of the box with last IntelliJ Ultimate Edition (Free 30-day trial).


# 1. Testing (persistence)
1. given
- test data
- mocks configuration
2. when
- object under test configuration
- execution
3. then
- expected
- asserts
- actual - select from db
- custom asserts

# 2. Clean business
- business case - spec. of makeScheduleForToday
- big picture of code OrderSchedulerService.makeScheduleForToday (tag: business-1)
- marked spec. elements: select orders, rules if-else implementation, balance assigement
- marked big win point - custom collections
- refactored version (tag: business-2)
- other enhancement techniks: sub methods, variables names
- simplify with technical elements (operators in properties, groovy rules)
- ConfigurationFactory, injection points
- groovy rules, injection of ruleset
- final pimped version (tag: business-3)


# 3. Testability

# 4. Extras

