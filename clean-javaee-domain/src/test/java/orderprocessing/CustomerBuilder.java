package orderprocessing;

import customermanagement.CustomerKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class CustomerBuilder {
    
    CustomerKey customerKey;
    
    public static CustomerBuilder aCustomer() {
        return new CustomerBuilder();
    }
    
    public CustomerBuilder likeCustomerWithoutOrders() {
        customerKey = new CustomerKey(13L);
        return this;
    }
    
    public CustomerEntity build() {
        CustomerEntity customer = new CustomerEntity();
        customer.customerKey = customerKey;
        return customer;
    }
    
}
