package orderprocessing.scheduling;

import inventory.ItemKey;

import java.util.Set;

import orderprocessing.OrderKey;


/**
 * Each AssignmentRule says: operator X can or can't prepare order Y.
 * <p>
 * Assignment rules examples:
 * 
 * <pre>
 * michal: order.orderKey.category == 'A1'
 * michal: order.orderLines.contains {it ~ '*tv*'}
 * </pre>
 * 
 * Example rule set says: michal can't prepare orders with category A1 or orders containing item
 * like *washing-machine*.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public interface AssignmentRule {
    
    boolean canPrepareOrder(String operator, OrderKey orderKey, Set<ItemKey> items);
}
