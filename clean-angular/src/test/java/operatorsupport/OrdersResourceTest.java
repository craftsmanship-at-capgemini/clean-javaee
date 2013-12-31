package operatorsupport;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import static orderprocessing.OrderBuilder.anOrder;
import orderprocessing.OrderKey;
import static orderprocessing.OrderSequenceBuilder.anOrderProcessingSequence;
import orderprocessing.OrderState;
import static org.fest.assertions.api.Assertions.assertThat;
import org.junit.Assume;
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
        String testDBProfile = System.getProperty("testing.persistence.test-db-profile");
        Assume.assumeTrue("test supports only postgresql database "
                + "(mvn ... -Dtesting.persistence.test-db-profile=postgresql)",
                "postgresql".equals(testDBProfile));

        Testing.inject(this);
    }

    @Test
    public void shouldReturnEmptySequence() {
        // given
        String operator = "michal";
        OrderKey someKey = new OrderKey("123400001", "B1", "2013");
        OrderKey[] emptySequence = {};
        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().withOrderKey(someKey).withOrderState(OrderState.OPEN).build(),
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

        OrderKey processedKey = new OrderKey("123456789", "A1", "2013");
        OrderKey scheduledKey1 = new OrderKey("987654321", "A2", "2013");
        OrderKey scheduledKey2 = new OrderKey("564738291", "A1", "2013");

        OrderKey[] sequencedKeys = {processedKey, scheduledKey1, scheduledKey2};
        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().withOrderKey(processedKey).withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(scheduledKey1).withOrderState(OrderState.SCHEDULED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(scheduledKey2).withOrderState(OrderState.SCHEDULED).build(),
                anOrderProcessingSequence(operator).withOrders(sequencedKeys).build()
        );

        // when
        String json = ordersResourceUnderTest.getOrders(operator);

        // then
        JsonArrayBuilder expectedJsonObjects = Json.createArrayBuilder();
        expectedJsonObjects.add(Json.createObjectBuilder()
                .add("key", "123456789/A1/2013")
                .add("state", "done")
                .add("products", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                .add("key", "ksr-14-378").add("done", 1).add("qty", 1))
                        .add(Json.createObjectBuilder()
                                .add("key", "jre-07-666").add("done", 2).add("qty", 2))
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

    @Test
    public void shouldNotReturnOpenAndClosedOrders() {
        // given
        String operator = "michal";

        OrderKey closedKey = new OrderKey("123400000", "B1", "2013");
        OrderKey processedKey = new OrderKey("123456789", "A1", "2013");
        OrderKey scheduledKey = new OrderKey("564738291", "A1", "2013");
        OrderKey openKey = new OrderKey("123400001", "B1", "2013");

        OrderKey[] sequencedKeys = {closedKey, processedKey, scheduledKey};

        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().withOrderKey(closedKey).withOrderState(OrderState.CLOSED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(processedKey).withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(scheduledKey).withOrderState(OrderState.SCHEDULED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(openKey).withOrderState(OrderState.OPEN).build(),
                anOrderProcessingSequence(operator).withOrders(sequencedKeys).build()
        );

        // when
        String json = ordersResourceUnderTest.getOrders(operator);

        // then
        assertThat(json)
                .doesNotContain(openKey.toString())
                .doesNotContain(closedKey.toString())
                .containsOnlyOnce(processedKey.toString())
                .containsOnlyOnce(scheduledKey.toString());
    }

    @Test
    public void shouldNotReturnOrdersAssignedToOtherOperator() {
        // given
        String michal = "michal";
        String krzysztof = "krzysztof";

        OrderKey michalKey1 = new OrderKey("123456789", "A1", "2013");
        OrderKey krzysztofKey1 = new OrderKey("564738291", "A1", "2013");
        OrderKey krzysztofKey2 = new OrderKey("123400000", "B1", "2013");
        OrderKey michalKey2 = new OrderKey("123400001", "B1", "2013");

        OrderKey[] sequencedKeysAssignedToMichal = {michalKey1, michalKey2};
        OrderKey[] sequencedKeysAssignedToKrzysztof = {krzysztofKey1, krzysztofKey2};

        persistenceUnit.persist(
                anOrder().likeSomeNew8ROrder().withOrderKey(michalKey1).withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(krzysztofKey1).withOrderState(OrderState.PROCESSED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(krzysztofKey2).withOrderState(OrderState.SCHEDULED).build(),
                anOrder().likeSomeNew8ROrder().withOrderKey(michalKey2).withOrderState(OrderState.SCHEDULED).build(),
                anOrderProcessingSequence(michal).withOrders(sequencedKeysAssignedToMichal).build(),
                anOrderProcessingSequence(krzysztof).withOrders(sequencedKeysAssignedToKrzysztof).build()
        );

        // when
        String json = ordersResourceUnderTest.getOrders(michal);

        // then
        assertThat(json)
                .doesNotContain(krzysztofKey1.toString())
                .doesNotContain(krzysztofKey2.toString())
                .containsOnlyOnce(michalKey1.toString())
                .containsOnlyOnce(michalKey2.toString());
    }

}
