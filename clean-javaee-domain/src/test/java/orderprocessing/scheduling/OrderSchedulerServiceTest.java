/**
 * 
 */
package orderprocessing.scheduling;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import inventory.ItemKey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.mockito.internal.util.reflection.Whitebox;

import testing.Testing;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrderSchedulerServiceTest {
    
    @Inject OrderSchedulerService service;
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
        OrderKey orderKeyNotProcessedByMichal = new OrderKey("0123123123", "A2", "213");
        ItemKey itemKeyNotProcessedByKasia = new ItemKey("tv-sams-54003-42");
        OrderEntity order = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderState(OrderState.OPEN).
                withOrderKey(orderKeyNotProcessedByMichal).
                withOrderLine(itemKeyNotProcessedByKasia, 1).build();
        when(repository.findNotDoneOrders()).thenReturn(Arrays.asList(order));
        
        Whitebox.setInternalState( service, "operators", operators );
        service.operators = operators;
        service.assignmentRules = assignmentRules;
        service.makeScheduleForToday();
        
        verify(repository, times(3)).persistOrderSequence(
                operatorCaptor.capture(),
                sequenceCaptor.capture());
        
        List<OrderKey> expectedSequence = Collections.singletonList(orderKeyNotProcessedByMichal);
        List<OrderKey> emptySequence = Collections.emptyList();
        
        Map<String, List<OrderKey>> orderKeySequenceByOperatorMap = buildOrderKeySequenceByOperatorMap(
                operatorCaptor.getAllValues(), sequenceCaptor.getAllValues());
        assertThat(orderKeySequenceByOperatorMap)
            .contains(
                entry("krzysztof", expectedSequence),
                entry("michal", emptySequence), 
                entry("kasia", emptySequence)
            );
        
        assertThat(order.getOrderState()).isEqualTo(OrderState.SCHEDULED);
    }
    
    @Test
    public void shouldAssignAllOrders() {
        List<OrderKey> expectedOrderKeys = Arrays.asList(
                new OrderKey("123456789", "H2", "2013"),
                new OrderKey("987654321", "A1", "2013"),
                new OrderKey("012345678", "AA", "2013"));
        
        List<OrderEntity> ordersToSchedule = Arrays.asList(
                OrderBuilder.anOrder().likeSomeNew8ROrder().
                        withOrderKey(expectedOrderKeys.get(0)).
                        withOrderState(OrderState.OPEN).build(),
                OrderBuilder.anOrder().likeSomeNew8ROrder().
                        withOrderKey(expectedOrderKeys.get(1)).
                        withOrderState(OrderState.OPEN).build(),
                OrderBuilder.anOrder().likeSomeNew8ROrder().
                        withOrderKey(expectedOrderKeys.get(2)).
                        withOrderState(OrderState.OPEN).build()
                );
        when(repository.findNotDoneOrders()).thenReturn(ordersToSchedule);
        
        service.operators = operators;
        service.assignmentRules = assignmentRules;
        service.makeScheduleForToday();
        
        verify(repository, times(3)).persistOrderSequence(
                operatorCaptor.capture(),
                sequenceCaptor.capture());
        
        Map<String, List<OrderKey>> orderKeySequenceByOperatorMap = buildOrderKeySequenceByOperatorMap(
                operatorCaptor.getAllValues(), sequenceCaptor.getAllValues());
        List<OrderKey> actualOrderKeys = new ArrayList<OrderKey>(expectedOrderKeys.size());
        actualOrderKeys.addAll(orderKeySequenceByOperatorMap.get("krzysztof"));
        actualOrderKeys.addAll(orderKeySequenceByOperatorMap.get("michal"));
        actualOrderKeys.addAll(orderKeySequenceByOperatorMap.get("kasia"));
        
        assertThat(actualOrderKeys).containsAll(expectedOrderKeys);
        
        assertThat(orderKeySequenceByOperatorMap.get("michal")).hasSize(1);
        assertThat(orderKeySequenceByOperatorMap.get("krzysztof")).hasSize(1);
        assertThat(orderKeySequenceByOperatorMap.get("kasia")).hasSize(1);
        
        for (OrderEntity order : ordersToSchedule) {
            assertThat(order.getOrderState()).isEqualTo(OrderState.SCHEDULED);
        }
    }
    
    private Map<String, List<OrderKey>> buildOrderKeySequenceByOperatorMap(List<String> operators,
            List<List<OrderKey>> sequences) {
        Map<String, List<OrderKey>> sequencesAsMap = new HashMap<String, List<OrderKey>>(operators.size());
        for (int i = 0; i < operators.size(); i++) {
            sequencesAsMap.put(operators.get(i), sequences.get(i));
        }
        return sequencesAsMap;
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
