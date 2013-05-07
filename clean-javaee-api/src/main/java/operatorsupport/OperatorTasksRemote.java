package operatorsupport;

import java.util.List;

import javax.ejb.Remote;

import orderprocessing.OrderKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Remote
public interface OperatorTasksRemote {
    
    List<OrderKey> getOrderSequence(String operator);
    
}
