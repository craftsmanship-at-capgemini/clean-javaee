package orderprocessing;

import javax.ejb.Remote;


/**
 * Service interface allows operators to mark progress of order preparation.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Remote
public interface OrderProgressManagementRemote {
    
    void orderDone(OrderKey orderKey);
    
}
