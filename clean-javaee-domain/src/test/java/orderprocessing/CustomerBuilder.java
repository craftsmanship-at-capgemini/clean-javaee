package orderprocessing;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import customermanagement.CustomerKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class CustomerBuilder {
    
    CustomerKey customerKey;
    Set<OrderEntity> orders = new HashSet<OrderEntity>();
    
    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder();
    }
    
    public CustomerBuilder likeCustomerWithoutOrders() {
        customerKey = new CustomerKey(3840576321L);
        return this;
    }
    
    public CustomerBuilder withCustomerKey(CustomerKey customerKey) {
        this.customerKey = customerKey;
        return this;
    }
    
    public CustomerBuilder withOrders(OrderEntity... orders) {
        this.orders.addAll(Arrays.asList(orders));
        return this;
    }
    
    public CustomerBuilder withOrders(OrderBuilder... builders) {
        for (OrderBuilder builder : builders) {
            this.orders.add(builder.build());
        }
        return this;
    }
    
    public CustomerBuilder withoutOrders() {
        this.orders = new HashSet<OrderEntity>();
        return this;
    }
    
    public CustomerEntity build() {
        CustomerEntity customer = new CustomerEntity();
        customer.customerKey = customerKey;
        customer.orders = orders;
        for (OrderEntity order : orders) {
            order.customer = customer;
        }
        return customer;
    }
    
}
