package orderprocessing.scheduling;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import orderprocessing.OrderKey;

import org.fest.assertions.api.MapAssert;
import org.mockito.ArgumentCaptor;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrderSequencesAssert extends MapAssert<String, List<OrderKey>> {
    
    private List<OrderKey> allScheduledOrderKeys = new LinkedList<OrderKey>();
    
    public OrderSequencesAssert(Map<String, List<OrderKey>> actual) {
        super(actual);
        for (List<OrderKey> sequence : actual.values()) {
            allScheduledOrderKeys.addAll(sequence);
        }
    }
    
    public static OrderSequencesAssert assertThat(Map<String, List<OrderKey>> actual) {
        return new OrderSequencesAssert(actual);
    }
    
    public static OrderSequencesAssert assertThat(ArgumentCaptor<String> operatorCaptor,
            ArgumentCaptor<List<OrderKey>> sequenceCaptor) {
        List<String> operators = operatorCaptor.getAllValues();
        List<List<OrderKey>> sequences = sequenceCaptor.getAllValues();
        Map<String, List<OrderKey>> sequencesAsMap = new HashMap<String, List<OrderKey>>(operators.size());
        for (int i = 0; i < operators.size(); i++) {
            sequencesAsMap.put(operators.get(i), sequences.get(i));
        }
        return new OrderSequencesAssert(sequencesAsMap);
    }
    
    public OrderSequencesAssert areAllKeysScheduled(OrderKey... orderKeys) {
        org.fest.assertions.api.Assertions.assertThat(allScheduledOrderKeys).containsAll(Arrays.asList(orderKeys));
        return this;
    }
    
    public OrderSequencesAssert areAllKeysScheduled(List<OrderKey> orderKeys) {
        org.fest.assertions.api.Assertions.assertThat(allScheduledOrderKeys).containsAll(orderKeys);
        return this;
    }
    
    public OrderSequencesAssert operatorSequenceHasSize(String operator, int expectedSize) {
        org.fest.assertions.api.Assertions.assertThat(actual.get(operator)).hasSize(expectedSize);
        return this;
    }
    
    public OrderSequencesAssert operatorHasSequence(String operator, List<OrderKey> sequence) {
        org.fest.assertions.api.Assertions.assertThat(actual.get(operator)).containsAll(sequence);
        return this;
    }
}
