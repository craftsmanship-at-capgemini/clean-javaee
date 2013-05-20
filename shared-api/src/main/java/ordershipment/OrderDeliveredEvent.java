package ordershipment;

import java.io.Serializable;

import orderprocessing.OrderKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 * 
 */
public class OrderDeliveredEvent implements Serializable {
    
    private static final long serialVersionUID = 5465544415372116594L;
    
    private OrderKey orderKey;
    
    public OrderDeliveredEvent(OrderKey orderKey) {
        this.orderKey = orderKey;
    }
    
    public OrderKey getOrderKey() {
        return orderKey;
    }
    
    @Override
    public String toString() {
        return "OrderDeliveredEvent [orderKey=" + orderKey + "]";
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orderKey == null) ? 0 : orderKey.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OrderDeliveredEvent other = (OrderDeliveredEvent) obj;
        if (orderKey == null) {
            if (other.orderKey != null)
                return false;
        } else if (!orderKey.equals(other.orderKey))
            return false;
        return true;
    }
    
}
