package orderprocessing.scheduling;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import orderprocessing.OrderEntity;
import orderprocessing.OrderRepository;
import orderprocessing.scheduling.utils.AssignmentRuleSet;
import orderprocessing.scheduling.utils.OperatorOrderAssignments;
import server.Configuration;

/**
 * {@link OrderSchedulerService} prepare daily schedule (order processing
 * sequences) for operators.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
public class OrderSchedulerService {
    
    @EJB OrderRepository orderRepository;
    
    @Inject @Configuration Set<String> operators;
    @Inject @Configuration Set<AssignmentRule> assignmentRules;
    
    /**
     * Creates order processing sequences for operators. Finds not done orders
     * and tries to assign them for processing to an appropriate operator.
     * 
     * Performs the following steps:
     * <ol>
     * <li>read all not done orders (treat scheduled but not processed orders as
     * not done)</li>
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
     * <li>delete existing order processing sequences</li>
     * <li>save the determined order processing sequences (also called
     * "schedule") for each operator</li>
     * </ol>
     */
    @Schedule(hour = "4")
    public void makeScheduleForToday() {
        List<OrderEntity> openOrders = orderRepository.findNotDoneOrders();
        
        OperatorOrderAssignments operatorAssignments = new OperatorOrderAssignments(operators);
        AssignmentRuleSet assignmentRuleSet = new AssignmentRuleSet(operators, assignmentRules);
        
        for (OrderEntity order : openOrders) {
            Set<String> possibleOperatorsForOrder = assignmentRuleSet.getPossibleOperators(order);
            
            if (!possibleOperatorsForOrder.isEmpty()) {
                String operatorToAssign = operatorAssignments
                        .retrieveOperatorWithMinUtilization(possibleOperatorsForOrder);
                
                operatorAssignments.assignOrderToOperator(operatorToAssign, order.getOrderKey(),
                        calculateOrderPreparationCost(order));
                
                order.markAsScheduled();
            }
        }
        
        orderRepository.deleteAllOrderProcessingSequences();
        for (String operator : operators) {
            orderRepository.persistOrderProcessingSequence(operator,
                    operatorAssignments.getOrderProcessingSequence(operator));
        }
    }
    
    private int calculateOrderPreparationCost(OrderEntity order) {
        return order.getOrderLines().size();
    }
    
}
