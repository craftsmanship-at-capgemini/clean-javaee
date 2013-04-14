package orderprocessing;

import static org.fest.assertions.api.Assertions.assertThat;
import static testing.Conditions.equalIgnoringIdAndVersion;

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
    
    @Inject OrderRepository orderRepository;
    
    private OrderKey favoriteOrderKey = new OrderKey("123456789", "H2", "2013");;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldFindOrderWhenExistsOnlyOne() throws NotFoundException {
        OrderKey orderKey = favoriteOrderKey;
        OrderEntity expected = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey(orderKey).build();
        persistenceUnit.persist(expected);
        
        OrderEntity actual = orderRepository.findOrder(orderKey);
        
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
        
        assertThat(actual).isNotSameAs(expected).
                is(equalIgnoringIdAndVersion(expected));
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
    
}
