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
import orderprocessing.OrderLineEntity;
import orderprocessing.OrderProgressManagementRemote;
import orderprocessing.OrderRepository;
import persistence.NotFoundException;
import server.Configuration;

/**
 * {@link OrderSchedulerService} prepare daily schedule for operators. Service
 * implements {@link OrderProgressManagementRemote} and allows operators to mark
 * progress of order preparation.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
public class OrderSchedulerService implements OrderProgressManagementRemote {
    
    @EJB OrderRepository orderRepository;
    
    @Inject @Configuration protected Set<String> operators;
    
    /**
     * <p>
     * <ol>
     * <li>treat scheduled but not processed orders as open
     * <li>read open orders
     * <li>split orders between operators
     * <li>assignment need to honor rules
     * <ul>
     * <li>operator kasia not process orders from category A1 and with item like
     * 'tv*'
     * <li>operator krzysiek process all orders
     * <li>operator michal process only orders from category A1
     * </ul>
     * <li>assignment should by balanced based on sum of items
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
            for (String operator : operators) {
                if (operator.equals("michal")) {
                    boolean passRule = order.getOrderKey().getCategory().equals("A1");
                    assignmentOptions.put(operator, passRule);
                    if (!passRule) {
                        continue;
                    }
                } else if (operator.equals("kasia")) {
                    if (order.getOrderKey().getCategory().equals("A1")) {
                        assignmentOptions.put(operator, false);
                        continue;
                    }
                    for (OrderLineEntity orderLine : order.getOrderLines()) {
                        if (orderLine.getItemKey().isLike("tv*")) {
                            assignmentOptions.put(operator, false);
                            continue;
                        }
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
                int orderCost = order.getOrderLines().size();
                int operatorAssigementCost = assignmentCost.get(assignedOperator);
                assignmentCost.put(assignedOperator, operatorAssigementCost + orderCost);
                
                order.markAsScheduled();
            }
        }
        orderRepository.deleteOrderSequences();
        for (String operator : assignments.keySet()) {
            orderRepository.persistOrderSequence(operator, assignments.get(operator));
        }
    }
    
    @Override
    public void orderDone(OrderKey orderKey) {
        try {
            OrderEntity order = orderRepository.findOrder(orderKey);
            // e.g. interact with delivery subsystem:
            // deliveryService.printShipmentLabel(orderKey, operatorPrinter)
            // deliveryService.orderReadyToShipment(orderKey)
            order.markAsProcessed();
        } catch (NotFoundException e) {
            // if order could by deleted in meanwhile and should not by
            // processed start emergency procedures e.g. send email to manager
        }
    }
    
}
