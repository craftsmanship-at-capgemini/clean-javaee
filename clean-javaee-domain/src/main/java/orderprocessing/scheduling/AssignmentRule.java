package orderprocessing.scheduling;

import orderprocessing.OrderEntity;
import server.BusinessRule;

/**
 * <p>
 * Each {@link AssignmentRule} says: operator X can't (or can only) prepare
 * order with properties Y.
 * <p>
 * Assignment rules examples:
 * <ul>
 * <li>michal can prepare only orders with category A1
 * <li>kasia can't prepare orders with category A1
 * <li>kasia can't prepare orders with item like 'tv*'
 * 
 * <p>
 * Rule should always return true if operator not match to rule.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@BusinessRule
public interface AssignmentRule {
    
    boolean canPrepareOrder(String operator, OrderEntity order);
}
