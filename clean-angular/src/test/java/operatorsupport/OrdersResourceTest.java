package operatorsupport;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import static orderprocessing.OrderBuilder.anOrder;
import orderprocessing.OrderEntity;
import orderprocessing.OrderKey;
import static orderprocessing.OrderSequenceBuilder.anOrderProcessingSequence;
import orderprocessing.OrderState;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import testing.Testing;
import testing.persistence.TestingPersistenceUnit;

/**
 *
 * @author michal
 */
public class OrdersResourceTest {

    @Rule
    public TestingPersistenceUnit persistenceUnit = new TestingPersistenceUnit("clean-javaee-test-db");

    @Inject
    OrdersResource ordersResourceUnderTest;

    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }

    @Test
    public void shouldReturnOrdersForMichalWhenOneSimpleSequenceExists() {
        // given
        String operator = "michal";
        OrderKey[] expectedOrderProcessingSequence = {
            new OrderKey("123456789", "A1", "2013"),
            new OrderKey("987654321", "A2", "2013"),
            new OrderKey("564738291", "A1", "2013")
        };

        persistenceUnit.persist(
                someScheduledOrdersWithKeys(expectedOrderProcessingSequence),
                anOrderProcessingSequence(operator).withOrders(expectedOrderProcessingSequence).build());
        // when
        String json = ordersResourceUnderTest.getOrders(operator);

        // then
        String expected = "["
                + "{\"key\":\"123456789/A1/2013\",\"state\":\"todo\","
                + "\"products\":["
                + "{\"key\":\"ksr-14-378\",\"done\":0,\"qty\":1},"
                + "{\"key\":\"jre-07-666\",\"done\":0,\"qty\":2}"
                + "]"
                + "},"
                + "{\"key\":\"987654321/A2/2013\",\"state\":\"todo\","
                + "\"products\":["
                + "{\"key\":\"ksr-14-378\",\"done\":0,\"qty\":1},"
                + "{\"key\":\"jre-07-666\",\"done\":0,\"qty\":2}"
                + "]"
                + "},"
                + "{\"key\":\"564738291/A1/2013\",\"state\":\"todo\","
                + "\"products\":["
                + "{\"key\":\"ksr-14-378\",\"done\":0,\"qty\":1},"
                + "{\"key\":\"jre-07-666\",\"done\":0,\"qty\":2}"
                + "]"
                + "}"
                + "]";
        assertThat(json).isEqualTo(expected);
    }

    private List<OrderEntity> someScheduledOrdersWithKeys(OrderKey[] expectedOrderProcessingSequence) {
        List<OrderEntity> orders = new ArrayList<>(expectedOrderProcessingSequence.length);
        for (OrderKey orderKey : expectedOrderProcessingSequence) {
            OrderEntity order = anOrder().likeSomeNew8ROrder()
                    .withOrderKey(orderKey).withOrderState(OrderState.SCHEDULED).build();
            orders.add(order);
        }
        return orders;
    }
}
