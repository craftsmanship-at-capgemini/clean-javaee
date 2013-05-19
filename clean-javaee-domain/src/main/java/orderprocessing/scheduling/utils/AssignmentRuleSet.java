package orderprocessing.scheduling.utils;

import java.util.HashSet;
import java.util.Set;

import orderprocessing.OrderEntity;
import orderprocessing.scheduling.AssignmentRule;
import server.BusinessRuleSet;

/**
 * {@link AssignmentRuleSet} aggregates results of rules for particular order.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 * 
 */
@BusinessRuleSet(aggregates = AssignmentRule.class)
public class AssignmentRuleSet {
    private final Set<String> operators;
    private final Set<AssignmentRule> assignmentRules;
    
    public AssignmentRuleSet(Set<String> operators, Set<AssignmentRule> assignmentRules) {
        this.operators = operators;
        this.assignmentRules = assignmentRules;
    }
    
    public Set<String> getPossibleOperators(OrderEntity order) {
        Set<String> possibleOperators = new HashSet<String>(operators.size());
        
        for (String operator : operators) {
            if (operatorCanPrepareOrder(operator, order)) {
                possibleOperators.add(operator);
            }
        }
        return operators;
    }
    
    private boolean operatorCanPrepareOrder(String operator, OrderEntity order) {
        for (AssignmentRule rule : assignmentRules) {
            boolean isRulePassed = rule.canPrepareOrder(operator, order);
            if (!isRulePassed) {
                return false;
            }
        }
        return true;
    }
}