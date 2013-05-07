/**
 * 
 */
package operatorsupport;

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import javax.inject.Inject;

import orderprocessing.OrderKey;
import orderprocessing.OrderSequenceBuilder;

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
    
    @Inject OperatorTaskDao operatorTaskDao;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void schouldReturnSequenceForMichalWhenOneSimpleSequenceExists() {
        String operator = "michal";
        OrderKey[] expected = {
                new OrderKey("123456789", "A1", "2013"),
                new OrderKey("987654321", "A2", "2013"),
                new OrderKey("564738291", "A1", "2013"),
        };
        persistenceUnit.persist(
                OrderSequenceBuilder.anOrderSequence(operator).
                        withOrders(expected).build());
        
        List<OrderKey> sequence = operatorTaskDao.getOrderSequence(operator);
        
        assertThat(sequence).containsExactly(expected);
    }
    
    @Test
    public void schouldReturnEmptySequenceWhenNoSequenceIsDefinedForGivenOperator() {
        String operator = "michal";
        String theOther = "krzysztof";
        persistenceUnit.persist(
                OrderSequenceBuilder.anOrderSequence(theOther).
                        withSomeOrders().build());
        
        List<OrderKey> sequence = operatorTaskDao.getOrderSequence(operator);
        
        assertThat(sequence).isEmpty();
    }
    
    @Test
    public void schouldReturnSequenceForCorrectOperatorWhenNoSequenceIsDefinedForGivenOperator() {
        String operator = "michal";
        String theOther = "krzysztof";
        OrderKey[] expected = {
                new OrderKey("123456789", "A1", "2013"),
                new OrderKey("987654321", "A2", "2013"),
                new OrderKey("564738291", "A1", "2013"),
        };
        persistenceUnit.persist(
                OrderSequenceBuilder.anOrderSequence(operator).
                        withOrders(expected).build(),
                OrderSequenceBuilder.anOrderSequence(theOther).
                        withSomeOrders().build());
        
        List<OrderKey> sequence = operatorTaskDao.getOrderSequence(operator);
        
        assertThat(sequence).containsExactly(expected);
    }
}
