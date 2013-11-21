package operatorsupport;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
import util.JsonUtils;

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
    public void shouldReturnEmptySequence() {
        // given
        String operator = "michal";
        OrderKey[] emptySequence = {};
        persistenceUnit.persist(
                anOrderProcessingSequence(operator).withOrders(emptySequence).build());

        // when
        String json = ordersResourceUnderTest.getOrders(operator);

        // then
        String expected = "[]";
        assertThat(json).isEqualTo(expected);
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
                anOrderProcessingSequence(operator).withOrders(expectedOrderProcessingSequence).build()
        );

        // when
        String json = ordersResourceUnderTest.getOrders(operator);

        // then
        JsonArrayBuilder expectedJsonObjects = Json.createArrayBuilder();
        expectedJsonObjects.add(Json.createObjectBuilder()
                .add("key", "123456789/A1/2013")
                .add("state", "todo")
                .add("products", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("key", "ksr-14-378").add("done", 0).add("qty", 1))
                        .add(Json.createObjectBuilder()
                                .add("key", "jre-07-666").add("done", 0).add("qty", 2))
                )
        ).add(Json.createObjectBuilder()
                .add("key", "987654321/A2/2013")
                .add("state", "todo")
                .add("products", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("key", "ksr-14-378").add("done", 0).add("qty", 1))
                        .add(Json.createObjectBuilder()
                                .add("key", "jre-07-666").add("done", 0).add("qty", 2))
                )
        ).add(Json.createObjectBuilder()
                .add("key", "564738291/A1/2013")
                .add("state", "todo")
                .add("products", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("key", "ksr-14-378").add("done", 0).add("qty", 1))
                        .add(Json.createObjectBuilder()
                                .add("key", "jre-07-666").add("done", 0).add("qty", 2))
                )
        );
        String expected = JsonUtils.asJsonString(expectedJsonObjects);

        assertThat(json)
                .isEqualTo(expected);
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
