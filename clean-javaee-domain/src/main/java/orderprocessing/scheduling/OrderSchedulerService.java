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
     * Creates a schedule of order processing for operators. Finds open orders
     * and tries to assign them for processing to an appropriate operator.
     * 
     * Performs the following steps:
     * <ol>
     * <li>read all open orders (treat scheduled but not processed orders as
     * open)</li>
     * <li>assign read orders to different operators - assignment must comply
     * with the following rules:</li>
     * <ul>
     * <li>operator kasia doesn't process orders from category A1 and with item
     * like 'tv*'</li>
     * <li>operator krzysiek processes all orders</li>
     * <li>operator michal processes only orders from category A1</li>
     * <li>assignment should be balanced between all operators based on sum of
     * items</li>
     * </ul>
     * <li>mark assigned orders as scheduled</li>
     * <li>delete existing Order Processing Sequences</li>
     * <li>save the determined Order Processing Sequences (also called
     * "schedule") for each operator</li>
     * </ol>
     */
    @Schedule(hour = "4")
    protected void makeScheduleForToday() {
        List<OrderEntity> openOrders = orderRepository.findNotDoneOrders();
        OperatorOrderAssignments operatorAssignments = new OperatorOrderAssignments(operators);
        for (OrderEntity order : openOrders) {
            Set<String> possibleOperatorsForOrder = determinePossibleOperatorsForOrder(order);
            if (!possibleOperatorsForOrder.isEmpty()) {
                String operatorToAssign = operatorAssignments
                        .retrieveOperatorWithMinUtilization(possibleOperatorsForOrder);
                operatorAssignments.assignOrderToOperator(operatorToAssign, order.getOrderKey(),
                        calculateOrderPreparationCost(order));
                order.markAsScheduled();
            }
        }
        orderRepository.deleteAllOrderSequences();
        for (String operator : operators) {
            orderRepository.persistOrderProcessingSequence(operator,
                    operatorAssignments.getOrderKeysOfOperator(operator));
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
    
    private Set<String> determinePossibleOperatorsForOrder(OrderEntity order) {
        RulesWhichOperatorCanPrepareOrder canOperatorPrepareOrderRules = new RulesWhichOperatorCanPrepareOrder(
                operators, assignmentRules, order);
        return canOperatorPrepareOrderRules.getOperatorsAllowedToPrepareOrder();
    }
    
    private int calculateOrderPreparationCost(OrderEntity order) {
        return order.getOrderLines().size();
    }
    
}

class OperatorOrderAssignments {
    Map<String, List<OrderKey>> orderKeysByOperator;
    Map<String, Integer> utilizationOfOperator;
    
    public OperatorOrderAssignments(Set<String> operators) {
        this.orderKeysByOperator = new HashMap<String, List<OrderKey>>(operators.size());
        for (String operator : operators) {
            orderKeysByOperator.put(operator, new LinkedList<OrderKey>());
        }
        utilizationOfOperator = new HashMap<String, Integer>(operators.size());
        for (String operator : operators) {
            utilizationOfOperator.put(operator, 0);
        }
    }
    
    public String retrieveOperatorWithMinUtilization(Set<String> operators) {
        String operatorWithMinUtilization = null;
        int minUtilization = Integer.MAX_VALUE;
        
        for (String operator : operators) {
            if (utilizationOfOperator.get(operator) < minUtilization) {
                minUtilization = utilizationOfOperator.get(operator);
                operatorWithMinUtilization = operator;
            }
        }
        return operatorWithMinUtilization;
    }
    
    public void assignOrderToOperator(String assignedOperator, OrderKey orderKey, int orderPreparationCost) {
        orderKeysByOperator.get(assignedOperator).add(orderKey);
        int currentUtilization = utilizationOfOperator.get(assignedOperator);
        utilizationOfOperator.put(assignedOperator, currentUtilization + orderPreparationCost);
        
    }
    
    public List<OrderKey> getOrderKeysOfOperator(String operator) {
        return orderKeysByOperator.get(operator);
    }
    
}

class RulesWhichOperatorCanPrepareOrder {
    Map<String, Boolean> canPrepareOrderFlagByOperator;
    
    public RulesWhichOperatorCanPrepareOrder(Set<String> operators, Set<AssignmentRule> assignmentRules,
            OrderEntity order) {
        this.canPrepareOrderFlagByOperator = new HashMap<String, Boolean>(operators.size());
        
        markOperatorsAsAllowedToPrepareOrderByDefault(operators);
        
        for (String operator : operators) {
            for (AssignmentRule rule : assignmentRules) {
                boolean isRulePassed = rule.canPrepareOrder(operator, order);
                if (!isRulePassed) {
                    markOperatorAsNotAllowedToPrepareOrder(operator);
                    break;
                }
            }
        }
    }
    
    public Set<String> getOperatorsAllowedToPrepareOrder() {
        Set<String> operators = new HashSet<String>();
        for (String operator : canPrepareOrderFlagByOperator.keySet()) {
            if (canPrepareOrderFlagByOperator.get(operator)) {
                operators.add(operator);
            }
        }
        return operators;
    }
    
    private void markOperatorsAsAllowedToPrepareOrderByDefault(Set<String> operators) {
        for (String operator : operators) {
            canPrepareOrderFlagByOperator.put(operator, true);
        }
    }
    
    private void markOperatorAsNotAllowedToPrepareOrder(String operator) {
        canPrepareOrderFlagByOperator.put(operator, false);
    }
}