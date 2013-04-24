package orderprocessing.scheduling;

import java.util.List;
import java.util.Set;

import javax.ejb.Remote;

import orderprocessing.OrderKey;

/**
 * TODO MM Write comment to type OrderSchedulerRemote
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Remote
public interface OrderSchedulerRemote {
    
    void lockOrder(OrderKey orderKey);
    
    void unlockOrder(OrderKey orderKey);
    
    List<OrderKey> getOrderSequence(String operator);
    
    Set<OrderKey> getLockedOrders();
}
