package orderprocessing;

import javax.ejb.Remote;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Remote
public interface OrderProgressManagementRemote {
    
    void orderDone(OrderKey orderKey);
    
}
