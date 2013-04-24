package orderprocessing.scheduling;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import orderprocessing.OrderKey;
import orderprocessing.OrderRepository;

/**
 * TODO KM Write comment to type OrderSchedulerService
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
public class OrderSchedulerService implements OrderSchedulerRemote {
    
    @EJB OrderRepository orderRepository;
    @Inject Set<String> operators;
    @Inject Set<AssignmentRule> assignmentRules;
    @Inject OrderWeights orderWeights;
    
    @Schedule(hour = "4")
    protected void makeSchedulForToday() {
        // TODO KM Auto-generated method stub
        // read not closed orders
        // split orders (locked too) between operators:
        //   operator kasia not process orders from category A1 and with item like *tv*
        //   operator krzysiek process all orders
        //   operator michal process only orders from category A1
        // split should by balanced (sum orderLine*weight)
        
        // mark orders as processed
        // persist orderSequence
    }
    
    @Override
    public void lockOrder(OrderKey orderKey) {
        // TODO KM Auto-generated method stub
        // mark orders as locked
        
    }
    
    @Override
    public void unlockOrder(OrderKey orderKey) {
        // TODO KM Auto-generated method stub
        // mark orders as processed
    }
    
    @Override
    public List<OrderKey> getOrderSequence(String operator) {
        // TODO KM Auto-generated method stub
        // read order sequence
        return null;
    }
    
    @Override
    public Set<OrderKey> getLockedOrders() {
        // TODO KM Auto-generated method stub
        // read locked orders
        return null;
    }
    
}
