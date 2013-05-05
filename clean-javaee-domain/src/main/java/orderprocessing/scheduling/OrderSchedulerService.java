package orderprocessing.scheduling;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import orderprocessing.OrderEntity;
import orderprocessing.OrderKey;
import orderprocessing.OrderRepository;
import orderprocessing.OrderState;
import server.Configuration;

/**
 * TODO KM Write comment to type OrderSchedulerService
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
public class OrderSchedulerService implements OrderSchedulerRemote {
    
    @EJB OrderRepository orderRepository;
    
    @Inject @Configuration protected Set<String> operators;
    @Inject @Configuration protected Set<AssignmentRule> assignmentRules;
    @Inject @Configuration protected ProcessingCostCalculator processingCostCalculator;
    
    /**
     * <p>
     * <ol>
     * <li>treat scheduled but not processed orders as open
     * <li>read open and locked orders
     * <li>split orders (locked too) between operators
     * <li>assignment need to honor rules
     * <ul>
     * <li>operator kasia not process orders from category A1 and with item like
     * 'tv*'
     * <li>operator krzysiek process all orders
     * <li>operator michal process only orders from category A1
     * </ul>
     * <li>assignment should by balanced (sum item*weight)
     * 
     * mark orders as processed
     */
    @Schedule(hour = "4")
    protected void makeScheduleForToday() {
        List<OrderEntity> openOrders = orderRepository.findNotDoneOrders();
        
        Map<String, List<OrderKey>> assignments =
                new HashMap<String, List<OrderKey>>(operators.size());
        for (String operator : operators) {
            assignments.put(operator, new LinkedList<OrderKey>());
        }
        
        Map<String, Integer> assignmentCost =
                new HashMap<String, Integer>(operators.size());
        for (String operator : operators) {
            assignmentCost.put(operator, 0);
        }
        
        for (OrderEntity order : openOrders) {
            Map<String, Boolean> assignmentOptions =
                    new HashMap<String, Boolean>(operators.size());
            for (String operator : operators) {
                assignmentOptions.put(operator, true);
            }
            OPERATOR: for (String operator : operators) {
                for (AssignmentRule rule : assignmentRules) {
                    boolean passRulle = rule.canPrepareOrder(operator, order);
                    boolean canPrepareOrder = assignmentOptions.get(operator);
                    
                    assignmentOptions.put(operator, canPrepareOrder && passRulle);
                    if (!passRulle) {
                        continue OPERATOR;
                    }
                }
            }
            String assignedOperator = null;
            int minAssigementCost = Integer.MAX_VALUE;
            for (String operator : operators) {
                boolean canPrepareOrder = assignmentOptions.get(operator);
                if (canPrepareOrder &&
                        assignmentCost.get(operator) < minAssigementCost) {
                    minAssigementCost = assignmentCost.get(operator);
                    assignedOperator = operator;
                }
            }
            if (assignedOperator != null) {
                assignments.get(assignedOperator).add(order.getOrderKey());
                int orderCost = processingCostCalculator.computeOrderCost(order);
                int operatorAssigementCost = assignmentCost.get(assignedOperator);
                assignmentCost.put(assignedOperator, operatorAssigementCost + orderCost);
                
                if (order.getOrderState() != OrderState.LOCKED) {
                    order.markAsScheduled();
                }
            }
        }
        orderRepository.deleteOrderSequences();
        for (String operator : assignments.keySet()) {
            orderRepository.persistOrderSequence(operator, assignments.get(operator));
        }
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
