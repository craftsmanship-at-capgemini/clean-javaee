package orderprocessing;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import persistence.EntityManagerHelper;
import persistence.NotFoundException;
import persistence.QueryParamBuilder;
import persistence.Repository;
import customermanagement.CustomerKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Stateless
@LocalBean
@Repository
public class OrderRepository {
    
    @PersistenceContext private EntityManager entityManager;
    
    public OrderEntity findOrder(OrderKey orderKey) throws NotFoundException {
        return EntityManagerHelper.findOne(entityManager, OrderEntity.class,
                "select distinct o from OrderEntity o left join fetch o.orderLines where o.orderKey = :orderKey",
                QueryParamBuilder.withParam("orderKey", orderKey));
    }
    
    public List<OrderEntity> findOrdersOfCustomer(CustomerKey customerKey) {
        return EntityManagerHelper.findMany(entityManager, OrderEntity.class,
                "select o from OrderEntity o where o.customer.customerKey = :customerKey",
                QueryParamBuilder.withParam("customerKey", customerKey));
    }
    
    public void deleteClosedOrders() {
        entityManager.createQuery("delete from OrderLineEntity ol where ol.order.orderState = :state")
                .setParameter("state", OrderState.CLOSED)
                .executeUpdate();
        entityManager.createQuery("delete from OrderEntity o where o.orderState = :state")
                .setParameter("state", OrderState.CLOSED)
                .executeUpdate();
    }
    
    public List<OrderEntity> findNotDoneOrders() {
        return EntityManagerHelper.findMany(entityManager, OrderEntity.class,
                "select o from OrderEntity o where o.orderState in (:open, :scheduled)",
                QueryParamBuilder.withParams(2).
                        param("open", OrderState.OPEN).
                        param("scheduled", OrderState.SCHEDULED));
    }
    
    public void deleteOrderSequences() {
        entityManager.createQuery("delete from SequenceElementEntity os").
                executeUpdate();
    }
    
    public void persistOrderSequence(String operator, List<OrderKey> sequence) {
        for (int sequenceNumber = 0; sequenceNumber < sequence.size(); sequenceNumber++) {
            SequenceElementEntity entity =
                    new SequenceElementEntity(operator, sequenceNumber, sequence.get(sequenceNumber));
            entityManager.persist(entity);
        }
    }
}
