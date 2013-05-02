package orderprocessing.scheduling;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import orderprocessing.OrderKey;

/**
 * TODO KM Write comment to type OrderSchedulerService
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
public class OrderSchedulerService implements OrderSchedulerRemote {
    
    @Override
    public void lockOrder(OrderKey orderKey) {
        // TODO KM Auto-generated method stub
        // mark orders as locked
        
    }
    
    @Override
    public void unlockOrder(OrderKey orderKey) {
        // TODO KM Auto-generated method stub
        // mark orders as scheduled
    }
    
    @Override
    public void markOrderAsProcessed(OrderKey orderKey) {
        // TODO KM Auto-generated method stub
        // mark orders as processed
    }
    
    @Override
    public List<OrderKey> getOrderSequence(String operator) {
        return Arrays.asList(
                new OrderKey("516244215", "8R", "2012"),
                new OrderKey("516244215", "8R", "2012"),
                new OrderKey("516244215", "8R", "2012"),
                new OrderKey("516244215", "8R", "2012")
                );
    }
    
    @Override
    public Set<OrderKey> getLockedOrders() {
        return null;
    }
}
