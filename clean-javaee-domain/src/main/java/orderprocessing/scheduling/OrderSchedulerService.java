package orderprocessing.scheduling;

import java.util.HashMap;
import java.util.HashSet;
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
    
    @Inject @Configuration Set<String> operators;
    @Inject @Configuration Set<AssignmentRule> assignmentRules;
    
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
     * <li>mark orders as scheduled
     */
    @Schedule(hour = "4")
    protected void makeScheduleForToday() {
        List<OrderEntity> openOrders = orderRepository.findNotDoneOrders();
        Assignments assignments = new Assignments(operators);
        for (OrderEntity order : openOrders) {
            AssignmentOptions assignmentOptions = new AssignmentOptions(operators);
            OPERATOR_LOOP: //
            for (String operator : operators) {
                for (AssignmentRule rule : assignmentRules) {
                    boolean isRulePassed = rule.canPrepareOrder(operator, order);
                    if (!isRulePassed) {
                        assignmentOptions.cantPrepareOrder(operator);
                        continue OPERATOR_LOOP;
                    }
                }
            }
            Set<String> possibleOperators = assignmentOptions.getPossibleOperators();
            if (!possibleOperators.isEmpty()) {
                String assignedOperator =
                        assignments.operatorWithMinUtilization(possibleOperators);
                assignments.assign(assignedOperator, order.getOrderKey(),
                        calculateOrderPreparationCost(order));
                order.markAsScheduled();
            }
        }
        orderRepository.deleteOrderSequences();
        for (String operator : operators) {
            orderRepository.persistOrderSequence(operator, assignments.getAssignments(operator));
        }
    }
    
    private int calculateOrderPreparationCost(OrderEntity order) {
        return order.getOrderLines().size();
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

class Assignments {
    Map<String, List<OrderKey>> assignments;
    Map<String, Integer> utilization;
    
    public Assignments(Set<String> operators) {
        this.assignments = new HashMap<String, List<OrderKey>>(operators.size());
        for (String operator : operators) {
            assignments.put(operator, new LinkedList<OrderKey>());
        }
        utilization = new HashMap<String, Integer>(operators.size());
        for (String operator : operators) {
            utilization.put(operator, 0);
        }
    }
    
    public String operatorWithMinUtilization(Set<String> operators) {
        String operatorWithMinUtilization = null;
        int minUtilization = Integer.MAX_VALUE;
        
        for (String operator : operators) {
            if (utilization.get(operator) < minUtilization) {
                minUtilization = utilization.get(operator);
                operatorWithMinUtilization = operator;
            }
        }
        return operatorWithMinUtilization;
    }
    
    public void assign(String assignedOperator, OrderKey orderKey, int orderPreparationCost) {
        assignments.get(assignedOperator).add(orderKey);
        int currentUtilization = utilization.get(assignedOperator);
        utilization.put(assignedOperator, currentUtilization + orderPreparationCost);
        
    }
    
    public List<OrderKey> getAssignments(String operator) {
        return assignments.get(operator);
    }
    
}

class AssignmentOptions {
    Map<String, Boolean> assignmentOptions;
    
    public AssignmentOptions(Set<String> operators) {
        this.assignmentOptions = new HashMap<String, Boolean>(operators.size());
        for (String operator : operators) {
            assignmentOptions.put(operator, true);
        }
    }
    
    public void cantPrepareOrder(String operator) {
        assignmentOptions.put(operator, false);
    }
    
    public Set<String> getPossibleOperators() {
        Set<String> operators = new HashSet<String>();
        for (String operator : assignmentOptions.keySet()) {
            if (assignmentOptions.get(operator)) {
                operators.add(operator);
            }
        }
        return operators;
    }
}