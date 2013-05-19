package orderprocessing;

import static com.google.code.liquidform.LiquidForm.alias;
import static com.google.code.liquidform.LiquidForm.select;
import static orderprocessing.OrderBuilder.anOrder;
import static org.fest.assertions.api.Assertions.assertThat;
import static testing.Conditions.equalIgnoringIdAndVersion;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.PersistenceUnitUtil;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import persistence.NotFoundException;
import testing.Testing;
import testing.persistence.TestingPersistenceUnit;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrderRepositoryTest {
    
    @Rule public ExpectedException thrown = ExpectedException.none();
    @Rule public TestingPersistenceUnit persistenceUnit = new TestingPersistenceUnit("clean-javaee-test-db");
    
    @Inject OrderRepository orderRepositoryUnderTest;
    
    private OrderKey favoriteOrderKey = new OrderKey("123456789", "H2", "2013");
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldFindOrderWhenExistsOnlyOne() throws NotFoundException {
        // given
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expectedOrder = anOrder().likeSomeNew8ROrder().but().
                withOrderKey(orderKey).build();
        
        persistenceUnit.persist(expectedOrder);
        
        // when
        OrderEntity actual = orderRepositoryUnderTest.findOrder(orderKey);
        
        // then
        // .isNotSameAs(expected) is only demonstrational
        // is(equalIgnoringIdAndVersion(expected)) ignores equals() method
        // ignoring id and version is not important by persisted entities
        assertThat(actual).isNotSameAs(expectedOrder).
                is(equalIgnoringIdAndVersion(expectedOrder));
    }
    
    @Test
    public void shouldFindOrderWhenManyExists() throws NotFoundException {
        // given
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expectedOrder = anOrder().likeSomeNew8ROrder().but().
                withOrderKey(orderKey).build();
        
        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().build(),
                expectedOrder,
                anOrder().likeSomeProcessed7KOrder().build());
        
        // when
        OrderEntity actual = orderRepositoryUnderTest.findOrder(orderKey);
        
        // then
        assertThat(actual).isEqualTo(expectedOrder);
    }
    
    @Test
    public void shouldThrowExceptionWhenLookedOrderNotExists() throws NotFoundException {
        // given
        OrderKey orderKeyOfNotExistingOrder = favoriteOrderKey;
        
        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().build(),
                anOrder().likeSomeProcessed7KOrder().build());
        
        // then
        thrown.expect(NotFoundException.class);
        
        // when
        orderRepositoryUnderTest.findOrder(orderKeyOfNotExistingOrder);
    }
    
    @Test
    public void shouldEgarFethOrderLinesWhenLookedForOneOrder() throws NotFoundException {
        // given
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expectedOrder = anOrder().likeSomeNew8ROrder().but().
                withOrderKey(orderKey).build();
        
        persistenceUnit.persist(expectedOrder);
        
        // when
        OrderEntity actualOrder = orderRepositoryUnderTest.findOrder(orderKey);
        
        // then
        PersistenceUnitUtil persistenceUnitUtil = persistenceUnit.getPersistenceUnitUtil();
        Assert.assertTrue(persistenceUnitUtil.isLoaded(actualOrder));
        Assert.assertTrue(persistenceUnitUtil.isLoaded(actualOrder, "orderLines"));
    }
    
    @Test
    public void shouldDoNothingWhenThereIsNonClosedOrder() {
        // given
        List<OrderEntity> expectedOrders = Arrays.asList(
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.OPEN).build(),
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.SCHEDULED).build(),
                anOrder().likeSomeProcessed7KOrder().
                        withOrderState(OrderState.PROCESSED).build());
        
        persistenceUnit.persist(expectedOrders);
        
        // when
        persistenceUnit.begin();
        orderRepositoryUnderTest.deleteClosedOrders();
        persistenceUnit.commit();
        
        // then
        OrderEntity o = alias(OrderEntity.class, "o");
        List<OrderEntity> actualOrders = persistenceUnit.executeQuery(
                select(o).from(OrderEntity.class).as(o));
        
        assertThat(actualOrders).containsAll(expectedOrders);
    }
    
    @Test
    public void shouldDeleteClosedOrders() {
        // given
        OrderEntity expectedOrderInNotClosedState = anOrder().likeSomeNew8ROrder().but().
                withOrderState(OrderState.SCHEDULED).build();
        
        List<OrderEntity> ordersToDelete = Arrays.asList(
                anOrder().likeSomeNew8ROrder().but().
                        withOrderKey(favoriteOrderKey).
                        withOrderState(OrderState.CLOSED).build(),
                anOrder().likeSomeProcessed7KOrder().but().
                        withOrderState(OrderState.CLOSED).build());
        
        persistenceUnit.persist(expectedOrderInNotClosedState, ordersToDelete);
        
        // when
        persistenceUnit.begin();
        orderRepositoryUnderTest.deleteClosedOrders();
        persistenceUnit.commit();
        
        // then
        OrderEntity o = alias(OrderEntity.class, "o");
        List<OrderEntity> actualOrders = persistenceUnit.executeQuery(
                select(o).from(OrderEntity.class).as(o));
        
        assertThat(actualOrders).containsOnly(expectedOrderInNotClosedState);
    }
    
    @Test
    public void shouldFindOpenOrderWhenOnlyOneAndOpenOrderExists() throws NotFoundException {
        // given
        OrderEntity expectedOpenOrder = anOrder().likeSomeNew8ROrder().but().
                withOrderState(OrderState.OPEN).build();
        
        persistenceUnit.persist(expectedOpenOrder);
        
        // when
        List<OrderEntity> actualNotDoneOrders = orderRepositoryUnderTest.findNotDoneOrders();
        
        // then
        assertThat(actualNotDoneOrders).containsOnly(expectedOpenOrder);
    }
    
    @Test
    public void shouldFindScheduledOrderWhenOnlyOneAndScheduledOrderExists() throws NotFoundException {
        // given
        OrderEntity expectedScheduledOrder = anOrder().likeSomeNew8ROrder().but().
                withOrderState(OrderState.SCHEDULED).build();
        
        persistenceUnit.persist(expectedScheduledOrder);
        
        // when
        List<OrderEntity> actualNotDoneOrders = orderRepositoryUnderTest.findNotDoneOrders();
        
        // then
        assertThat(actualNotDoneOrders).containsOnly(expectedScheduledOrder);
    }
    
    @Test
    public void shouldNotFindDoneOrdersWhenOnlyDoneOrderExist() throws NotFoundException {
        // given
        List<OrderEntity> doneOrders = Arrays.asList(
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.CLOSED).build());
        
        persistenceUnit.persist(doneOrders);
        
        // when
        List<OrderEntity> actualNotDoneOrders = orderRepositoryUnderTest.findNotDoneOrders();
        
        // then
        assertThat(actualNotDoneOrders).isEmpty();
    }
    
    @Test
    public void shouldFindOpenOrderWhenManyExists() throws NotFoundException {
        // given
        List<OrderEntity> expectedNotDoneOrders = Arrays.asList(
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.OPEN).build(),
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.SCHEDULED).build());
        
        OrderEntity[] doneOrders = {
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().but().
                        withOrderState(OrderState.CLOSED).build() };
        
        persistenceUnit.persist(expectedNotDoneOrders, Arrays.asList(doneOrders));
        
        // when
        List<OrderEntity> actualNotDoneOrders = orderRepositoryUnderTest.findNotDoneOrders();
        
        // then
        assertThat(actualNotDoneOrders).
                containsAll(expectedNotDoneOrders).
                doesNotContain(doneOrders);
    }
}
