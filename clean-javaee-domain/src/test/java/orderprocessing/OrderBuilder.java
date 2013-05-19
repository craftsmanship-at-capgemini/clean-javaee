package orderprocessing;

import inventory.ItemKey;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import testing.data.TestDataBuilder;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@TestDataBuilder(OrderEntity.class)
public class OrderBuilder {
    
    private OrderKey orderKey;
    private OrderState orderState;
    private CustomerEntity customer;
    private Date creationTime;
    private Set<OrderLineEntity> orderLines = new HashSet<OrderLineEntity>();
    
    static public OrderBuilder anOrder() {
        return new OrderBuilder();
    }
    
    public OrderBuilder likeSomeNew8ROrder() {
        orderKey = new OrderKey("516244215", "8R", "2013");
        orderState = OrderState.OPEN;
        customer = CustomerBuilder.aCustomer().likeCustomerWithoutOrders().build();
        creationTime = new Date();
        withSomeOrderLines1();
        return this;
    }
    
    public OrderBuilder likeSomeProcessed7KOrder() {
        orderKey = new OrderKey("421551624", "7K", "2013");
        orderState = OrderState.PROCESSED;
        customer = CustomerBuilder.aCustomer().likeCustomerWithoutOrders().build();
        
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(new Date());
        gc.add(Calendar.DAY_OF_YEAR, -1);
        creationTime = gc.getTime();
        
        withSomeOrderLines2();
        return this;
    }
    
    public OrderBuilder but() {
        return this;
    }
    
    public OrderBuilder withOrderKey(OrderKey orderKey) {
        this.orderKey = orderKey;
        return this;
    }
    
    public OrderBuilder withOrderKey(String key, String category, String year) {
        orderKey = new OrderKey(key, category, year);
        return this;
    }
    
    public OrderBuilder withOrderState(OrderState orderState) {
        this.orderState = orderState;
        return this;
    }
    
    public OrderBuilder withCustomer(CustomerEntity customer) {
        this.customer = customer;
        return this;
    }
    
    public OrderBuilder withCustomer(CustomerBuilder customerBuilder) {
        this.customer = customerBuilder.build();
        return this;
    }
    
    public OrderBuilder withOrderLine(ItemKey itemKey, int quantity) {
        orderLines.add(new OrderLineEntity(null, itemKey, quantity));
        return this;
    }
    
    public OrderBuilder withoutOrderLines() {
        orderLines = new HashSet<OrderLineEntity>();
        return this;
    }
    
    public OrderBuilder withSomeOrderLines1() {
        orderLines.add(new OrderLineEntity(null, new ItemKey("ksr-14-378"), 1));
        orderLines.add(new OrderLineEntity(null, new ItemKey("jre-07-666"), 2));
        return this;
    }
    
    public OrderBuilder withSomeOrderLines2() {
        orderLines.add(new OrderLineEntity(null, new ItemKey("wro-71-000"), 1));
        orderLines.add(new OrderLineEntity(null, new ItemKey("waw-22-111"), 1));
        orderLines.add(new OrderLineEntity(null, new ItemKey("poz-61-222"), 1));
        return this;
    }
    
    public OrderEntity build() {
        OrderEntity order = new OrderEntity();
        order.orderKey = orderKey;
        order.orderState = orderState;
        order.customer = customer;
        customer.orders.add(order);
        order.creationTime = creationTime;
        order.orderLines = orderLines;
        for (OrderLineEntity orderLine : orderLines) {
            orderLine.order = order;
        }
        return order;
    }
}
