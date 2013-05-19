package orderprocessing.scheduling.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import orderprocessing.OrderKey;

/**
 * {@link OperatorOrderAssignments} is a collection of order processing
 * sequences with utilization pro operator.
 * 
 * Utilization is measure of work needed to complete order processing sequence.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 * 
 */
public class OperatorOrderAssignments {
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
    
    public List<OrderKey> getOrderProcessingSequence(String operator) {
        return orderKeysByOperator.get(operator);
    }
    
}