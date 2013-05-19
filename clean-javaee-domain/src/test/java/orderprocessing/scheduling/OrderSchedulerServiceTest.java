package orderprocessing.scheduling;

import static orderprocessing.OrderBuilder.anOrder;
import static orderprocessing.scheduling.OrderSequencesAssert.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import inventory.ItemKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import orderprocessing.OrderBuilder;
import orderprocessing.OrderEntity;
import orderprocessing.OrderKey;
import orderprocessing.OrderLineEntity;
import orderprocessing.OrderRepository;
import orderprocessing.OrderState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import testing.Testing;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrderSchedulerServiceTest {
    
    @Inject OrderSchedulerService orderSchedulerServiceUnderTest;
    
    @Mock OrderRepository repository;
    @Captor ArgumentCaptor<String> operatorCaptor;
    @Captor ArgumentCaptor<List<OrderKey>> sequenceCaptor;
    
    Set<String> operators = createOperators();
    Set<AssignmentRule> assignmentRules = createOriginalAssignmentRule();
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldAssignSingleOrderToKrzysztof() {
        // given
        OrderKey orderKeyNotProcessedByMichal = new OrderKey("0123123123", "A2", "213");
        ItemKey itemKeyNotProcessedByKasia = new ItemKey("tv-sams-54003-42");
        List<OrderKey> expectedSequence = Collections.singletonList(orderKeyNotProcessedByMichal);
        List<OrderKey> emptySequence = Collections.emptyList();
        
        OrderEntity orderToSchedule = anOrder().likeSomeNew8ROrder().but().
                withOrderState(OrderState.OPEN).
                withOrderKey(orderKeyNotProcessedByMichal).
                withOrderLine(itemKeyNotProcessedByKasia, 1).build();
        
        when(repository.findNotDoneOrders()).thenReturn(Collections.singletonList(orderToSchedule));
        
        // when
        orderSchedulerServiceUnderTest.operators = operators;
        orderSchedulerServiceUnderTest.assignmentRules = assignmentRules;
        orderSchedulerServiceUnderTest.makeScheduleForToday();
        
        // then
        verify(repository, times(3)).persistOrderProcessingSequence(
                operatorCaptor.capture(),
                sequenceCaptor.capture());
        
        assertThat(operatorCaptor, sequenceCaptor).
                operatorHasSequence("michal", emptySequence).
                operatorHasSequence("krzysztof", expectedSequence).
                operatorHasSequence("kasia", emptySequence);
        
        assertThat(orderToSchedule.getOrderState()).isEqualTo(OrderState.SCHEDULED);
    }
    
    @Test
    public void shouldAssignAllOrders() {
        // given
        List<OrderKey> expectedOrderKeys = Arrays.asList(
                new OrderKey("123456789", "H2", "2013"),
                new OrderKey("987654321", "A1", "2013"),
                new OrderKey("012345678", "AA", "2013"));
        
        List<OrderEntity> ordersToSchedule = createOpenOrdersWithKeys(expectedOrderKeys);
        
        when(repository.findNotDoneOrders()).thenReturn(ordersToSchedule);
        
        // when
        orderSchedulerServiceUnderTest.operators = operators;
        orderSchedulerServiceUnderTest.assignmentRules = assignmentRules;
        orderSchedulerServiceUnderTest.makeScheduleForToday();
        
        // then
        verify(repository, times(3)).persistOrderProcessingSequence(
                operatorCaptor.capture(),
                sequenceCaptor.capture());
        
        assertThat(operatorCaptor, sequenceCaptor).
                areAllKeysScheduled(expectedOrderKeys).
                operatorSequenceHasSize("michal", 1).
                operatorSequenceHasSize("krzysztof", 1).
                operatorSequenceHasSize("kasia", 1);
        
        for (OrderEntity order : ordersToSchedule) {
            assertThat(order.getOrderState()).isEqualTo(OrderState.SCHEDULED);
        }
    }
    
    private List<OrderEntity> createOpenOrdersWithKeys(List<OrderKey> orderKeys) {
        OrderBuilder orderBuilder = anOrder().likeSomeNew8ROrder().but().withOrderState(OrderState.OPEN);
        
        List<OrderEntity> ordersToSchedule = new ArrayList<OrderEntity>(orderKeys.size());
        for (OrderKey orderKey : orderKeys) {
            ordersToSchedule.add(
                    orderBuilder.but().withOrderKey(orderKey).build()
                    );
        }
        return ordersToSchedule;
    }
    
    private Set<String> createOperators() {
        return new HashSet<String>(Arrays.<String> asList("michal", "krzysztof", "kasia"));
    }
    
    private static Set<AssignmentRule> createOriginalAssignmentRule() {
        return new HashSet<AssignmentRule>(Arrays.asList(
                new AssignmentRule() {
                    @Override
                    public boolean canPrepareOrder(String operator, OrderEntity order) {
                        if (operator.equals("michal")) {
                            return order.getOrderKey().getCategory().equals("A1");
                        }
                        return true;
                    }
                },
                new AssignmentRule() {
                    @Override
                    public boolean canPrepareOrder(String operator, OrderEntity order) {
                        if (operator.equals("kasia")) {
                            if (order.getOrderKey().getCategory().equals("A1")) {
                                return false;
                            }
                            Set<OrderLineEntity> orderLines = order.getOrderLines();
                            for (OrderLineEntity orderLine : orderLines) {
                                if (orderLine.getItemKey().isLike("tv*")) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
                ));
    }
    
}
