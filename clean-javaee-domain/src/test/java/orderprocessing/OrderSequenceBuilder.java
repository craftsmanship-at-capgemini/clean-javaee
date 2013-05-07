package orderprocessing;

import java.util.LinkedList;
import java.util.List;

import testing.data.TestDataBuilder;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@TestDataBuilder(SequenceElementEntity.class)
public class OrderSequenceBuilder {
    
    private String operator;
    private int sequenceNumber = 0;
    private List<SequenceElementEntity> sequence = new LinkedList<SequenceElementEntity>();
    
    static public OrderSequenceBuilder anOrderSequence(String operator) {
        OrderSequenceBuilder orderSequenceBuilder = new OrderSequenceBuilder();
        orderSequenceBuilder.operator = operator;
        return orderSequenceBuilder;
    }
    
    public OrderSequenceBuilder withOrder(OrderKey orderKey) {
        sequence.add(new SequenceElementEntity(operator, sequenceNumber++, orderKey));
        return this;
    }
    
    public OrderSequenceBuilder withOrders(OrderKey... orderKeys) {
        for (OrderKey orderKey : orderKeys) {
            sequence.add(new SequenceElementEntity(operator, sequenceNumber++, orderKey));
        }
        return this;
    }
    
    public OrderSequenceBuilder withOrders(List<OrderKey> orderKeys) {
        for (OrderKey orderKey : orderKeys) {
            sequence.add(new SequenceElementEntity(operator, sequenceNumber++, orderKey));
        }
        return this;
    }
    
    public OrderSequenceBuilder withSomeOrders() {
        sequence.add(new SequenceElementEntity(operator, sequenceNumber++,
                new OrderKey("116844328", "4A", "2013")));
        sequence.add(new SequenceElementEntity(operator, sequenceNumber++,
                new OrderKey("516244215", "8R", "2013")));
        sequence.add(new SequenceElementEntity(operator, sequenceNumber++,
                new OrderKey("421551624", "7K", "2013")));
        return this;
    }
    
    public List<SequenceElementEntity> build() {
        return sequence;
    }
    
}
