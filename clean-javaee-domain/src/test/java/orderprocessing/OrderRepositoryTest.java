package orderprocessing;

import static com.google.code.liquidform.LiquidForm.alias;
import static com.google.code.liquidform.LiquidForm.select;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.atIndex;
import static testing.Conditions.equalIgnoringIdAndVersion;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceUnitUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.code.liquidform.FromClause;

import persistence.NotFoundException;
import testing.Testing;
import testing.persistence.TestingPersistenceUnit;
import customermanagement.CustomerKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrderRepositoryTest {
    
    @Rule public ExpectedException thrown = ExpectedException.none();
    @Rule public TestingPersistenceUnit persistenceUnit = new TestingPersistenceUnit("clean-javaee-test-db");
    
    @Inject OrderRepository orderRepository;
    
    private OrderKey favoriteOrderKey = new OrderKey("123456789", "H2", "2013");
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldFindOrderWhenExistsOnlyOne() throws NotFoundException {
        // given
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expected = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey(orderKey).build();
        persistenceUnit.persist(expected);

        // when
        OrderEntity actual = orderRepository.findOrder(orderKey);
        
        // then
        // .isNotSameAs(expected) is only demonstrational
        // is(equalIgnoringIdAndVersion(expected)) ignores equals() method
        // ignoring id and version is not important by persisted entities
        assertThat(actual).isNotSameAs(expected).
                is(equalIgnoringIdAndVersion(expected));
    }
    
    @Test
    public void shouldFindOrderWhenManyExists() throws NotFoundException {
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expected = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey(orderKey).build();
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                expected,
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        OrderEntity actual = orderRepository.findOrder(orderKey);
        
        assertThat(actual).
                isLenientEqualsToByIgnoringFields(expected);
    }
    
    @Test
    public void shouldThrowExceptionWhenLookedOrderNotExists() throws NotFoundException {
        OrderKey orderKey = favoriteOrderKey;
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        thrown.expect(NotFoundException.class);
        orderRepository.findOrder(orderKey);
    }
    
    @Test
    public void shouldFethOrderLinesWhenLookedForOneOrder() throws NotFoundException {
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expected = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey(orderKey).build();
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                expected,
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        OrderEntity actual = orderRepository.findOrder(orderKey);
        
        PersistenceUnitUtil persistenceUnitUtil = persistenceUnit.getPersistenceUnitUtil();
        Assert.assertTrue(persistenceUnitUtil.isLoaded(actual));
        Assert.assertTrue(persistenceUnitUtil.isLoaded(actual, "orderLines"));
    }
    
    @Test
    public void shouldFindSingleOrderWhenCustomerHasOneOrder() {
        CustomerKey customerKey = new CustomerKey(13L);
        OrderEntity expected = OrderBuilder.
                anOrder().likeSomeNew8ROrder().
                withCustomer(
                        CustomerBuilder.aCustomer().likeCustomerWithoutOrders().
                                withCustomerKey(customerKey)
                ).build();
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                expected,
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        List<OrderEntity> actual = orderRepository.findOrdersOfCustomer(customerKey);
        
        assertThat(actual).hasSize(1).
                has(equalIgnoringIdAndVersion(expected), atIndex(0));
    }
    
    @Test
    public void shouldFindAllCustomerOrdersAndIgnoreOthers() {
        // given
        CustomerKey customerKey = new CustomerKey(13L);
        OrderEntity expected1 = OrderBuilder.anOrder().likeSomeNew8ROrder()
                .withOrderKey("123456789", "AA", "2013").build();
        OrderEntity expected2 = OrderBuilder.anOrder().likeSomeProcessed7KOrder()
                .withOrderKey("012345678", "AA", "2013").build();
        OrderEntity order8R = OrderBuilder.anOrder().likeSomeNew8ROrder().build();
        CustomerEntity customer = CustomerBuilder.aCustomer().likeCustomerWithoutOrders()
                .withCustomerKey(customerKey).withOrders(expected1, expected2).build();
        OrderEntity order7K = OrderBuilder.anOrder().likeSomeProcessed7KOrder().build();
        persistenceUnit.persist(order8R, customer, order7K);

        // when
        List<OrderEntity> actual = orderRepository.findOrdersOfCustomer(customerKey);

        // then
        // ignores order of records but use equals!
        assertThat(actual).containsOnly(expected1, expected2);
    }
    
    @Test
    public void shouldReturnEmptyListWhenCustomerHasNoOrders() {
        CustomerKey customerKey = new CustomerKey(13L);
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                CustomerBuilder.aCustomer().likeCustomerWithoutOrders().
                        withCustomerKey(customerKey).build(),
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        List<OrderEntity> actual = orderRepository.findOrdersOfCustomer(customerKey);
        
        assertThat(actual).isEmpty();
    }
    
    @Test
    public void shouldReturnEmptyListWhenCustomerNotExists() {
        CustomerKey customerKey = new CustomerKey(13L);
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().build(),
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().build()
                );
        
        List<OrderEntity> actual = orderRepository.findOrdersOfCustomer(customerKey);
        
        assertThat(actual).isEmpty();
    }
    
    @Test
    public void shouldWorkWithoutErrorWhenNoOrdersInSystem() {
        persistenceUnit.begin();
        orderRepository.deleteClosedOrders();
        persistenceUnit.commit();
    }
    
    @Test
    public void shouldKeepAllNonClosedOrdersWhenDeletingClosedOrders() {
        OrderEntity expected1 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderState(OrderState.SCHEDULED).build();
        OrderEntity expected2 = OrderBuilder.anOrder().likeSomeProcessed7KOrder().
                withOrderState(OrderState.PROCESSED).build();
        persistenceUnit.persist(expected1, expected2);
        
        persistenceUnit.begin();
        orderRepository.deleteClosedOrders();
        persistenceUnit.commit();
        
        OrderEntity o = alias(OrderEntity.class, "o");
        @SuppressWarnings("unchecked")
        List<OrderEntity> actual = persistenceUnit.createQuery(
                select(o).from(OrderEntity.class).as(o)
                ).getResultList();
        
        assertThat(actual).
                containsOnly(expected1, expected2);
    }
    
    @Test
    public void shouldDeleteClosedOrdersOnlyAndKeepTheSheduledOrder() {
        OrderEntity orderToDelete1 = OrderBuilder.anOrder().likeSomeNew8ROrder()
                .withOrderKey(favoriteOrderKey)
                .withOrderState(OrderState.CLOSED).build();
        OrderEntity expectedNotDeletedOrder = OrderBuilder.anOrder().likeSomeNew8ROrder()
                .withOrderState(OrderState.SCHEDULED).build();
        OrderEntity orderToDelete2 = OrderBuilder.anOrder().likeSomeProcessed7KOrder()
                .withOrderState(OrderState.CLOSED).build();
        persistenceUnit.persist(orderToDelete1, expectedNotDeletedOrder, orderToDelete2);

        persistenceUnit.begin();
        orderRepository.deleteClosedOrders();
        persistenceUnit.commit();

        OrderEntity o = alias(OrderEntity.class, "o");
        FromClause<OrderEntity, OrderEntity> queryFromClause = select(o).from(OrderEntity.class).as(o);
        @SuppressWarnings("unchecked")
        List<OrderEntity> actual = persistenceUnit.createQuery(queryFromClause).getResultList();

        assertThat(actual).containsOnly(expectedNotDeletedOrder);
    }
    
    @Test
    public void shouldFindMultipleOpenOrdersWhenProcessedAndClosedOrdersExistAsWell() throws NotFoundException {
        OrderEntity expected1 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("123456789", "AA", "2013").withOrderState(OrderState.OPEN).build();
        OrderEntity expected2 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("012345678", "AA", "2013").withOrderState(OrderState.OPEN).build();
        // REV3: why not create all orders here as throughout the whole test class?
        persistenceUnit.persist(
                OrderBuilder.anOrder().likeSomeNew8ROrder().
                        withOrderState(OrderState.PROCESSED).build(),
                expected1,
                OrderBuilder.anOrder().likeSomeProcessed7KOrder().
                        withOrderState(OrderState.CLOSED).build(),
                expected2
                );
        
        List<OrderEntity> actual = orderRepository.findNotDoneOrders();
        
        assertThat(actual).containsOnly(expected1, expected2);
    }
}
