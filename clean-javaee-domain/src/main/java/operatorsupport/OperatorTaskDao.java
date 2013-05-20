package operatorsupport;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import orderprocessing.OrderKey;
import persistence.DataAccessObject;
import persistence.EntityManagerHelper;
import persistence.QueryParamBuilder;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 * 
 */
@Stateless(mappedName="operatorTasks")
@LocalBean
@DataAccessObject
public class OperatorTaskDao implements OperatorTasksRemote {
    
    @PersistenceContext private EntityManager entityManager;
    
    @Override
    public List<OrderKey> getOrderSequence(String operator) {
        return EntityManagerHelper.findManyDynamicTyped(
                entityManager, "select se.orderKey from SequenceElementEntity se " +
                        "where se.operator = :operator order by se.sequenceNumber",
                QueryParamBuilder.withParam("operator", operator));
    }
    
}
