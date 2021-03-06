package operatorsupport;

import static orderprocessing.OrderSequenceBuilder.anOrderProcessingSequence;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import orderprocessing.OrderKey;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import testing.Testing;
import testing.persistence.TestingPersistenceUnit;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OperatorTaskDaoTest {
    
    @Rule public TestingPersistenceUnit persistenceUnit = new TestingPersistenceUnit("clean-javaee-test-db");
    
    @Inject OperatorTasksDao operatorTasksDaoUnderTest;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldReturnSequenceForMichalWhenOneSimpleSequenceExists() {
        // given
        String operator = "michal";
        OrderKey[] expectedOrderProcessingSequence = {
                new OrderKey("123456789", "A1", "2013"),
                new OrderKey("987654321", "A2", "2013"),
                new OrderKey("564738291", "A1", "2013") };
        
        persistenceUnit.persist(
                anOrderProcessingSequence(operator).
                        withOrders(expectedOrderProcessingSequence).build());
        // when
        List<OrderKey> actualOrderProcessingSequence = operatorTasksDaoUnderTest.getOrderSequence(operator);
        
        // then
        assertThat(actualOrderProcessingSequence).containsExactly(expectedOrderProcessingSequence);
    }
    
    @Test
    public void shouldReturnEmptySequenceWhenNoSequenceIsDefinedForGivenOperator() {
        // given
        String operator = "michal";
        String otherOperator = "krzysztof";
        
        persistenceUnit.persist(
                anOrderProcessingSequence(otherOperator).
                        withSomeOrders().build());
        
        // when
        List<OrderKey> actualOrderProcessingSequence = operatorTasksDaoUnderTest.getOrderSequence(operator);
        
        // then
        assertThat(actualOrderProcessingSequence).isEmpty();
    }
    
    @Test
    public void shouldReturnSequenceForCorrectOperatorWhenNoSequenceIsDefinedForGivenOperator() {
        // given
        String operator = "michal";
        String otherOperator = "krzysztof";
        OrderKey[] expectedOrderProcessingSequence = {
                new OrderKey("123456789", "A1", "2013"),
                new OrderKey("987654321", "A2", "2013"),
                new OrderKey("564738291", "A1", "2013") };
        
        persistenceUnit.persist(
                anOrderProcessingSequence(operator).
                        withOrders(expectedOrderProcessingSequence).build(),
                anOrderProcessingSequence(otherOperator).
                        withSomeOrders().build());
        
        // when
        List<OrderKey> actualOrderProcessingSequence = operatorTasksDaoUnderTest.getOrderSequence(operator);
        
        // then
        assertThat(actualOrderProcessingSequence).containsExactly(expectedOrderProcessingSequence);
    }
}
