package operatorsupport;

import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import orderprocessing.OrderEntity;
import orderprocessing.OrderLineEntity;
import orderprocessing.OrderState;
import static orderprocessing.OrderState.PROCESSED;
import static orderprocessing.OrderState.SCHEDULED;
import persistence.EntityManagerHelper;
import persistence.QueryParamBuilder;
import util.JsonUtils;

/**
 *
 * @author michal
 */
@Path("/orders")
@RequestScoped
public class OrdersResource {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * <pre>
     * {
     * "key": "123456789/A1/2013",
     * "state": "todo",
     * "products": [
     * {"key": "ksr-14-378", "done":0, "qty":1},
     * {"key": "jre-07-666", "done":0, "qty":2}
     * ]
     * }
     * </pre>
     *
     */
    @GET
    @Path("/{operator}")
    @Produces("application/json")
    public String getOrders(@PathParam("operator") String operator) {
        List<OrderEntity> orders = EntityManagerHelper.findMany(entityManager, OrderEntity.class,
                "select distinct o from OrderEntity o join fetch o.rderLineEntity ol "
                + "join SequenceElementEntity se on (o.orderKey = se.orderKey) "
                + "where se.operator = :operator and o.orderState in (:scheduled, :processed) "
                + "order by se.sequenceNumber",
                QueryParamBuilder.withParams(3).param("operator", operator)
                .param("scheduled", SCHEDULED).param("processed", PROCESSED));

        JsonArrayBuilder ordersJson = Json.createArrayBuilder();
        for (OrderEntity order : orders) {
            JsonArrayBuilder productsJson = Json.createArrayBuilder();
            for (OrderLineEntity orderLine : order.getOrderLines()) {
                productsJson.add(Json.createObjectBuilder()
                        .add("key", orderLine.getItemKey().toString())
                        .add("done", 0)
                        .add("qty", orderLine.getQuantity())
                );
            }
            ordersJson.add(Json.createObjectBuilder()
                    .add("key", order.getOrderKey().toString())
                    .add("state", mapState(order.getOrderState()))
                    .add("products", productsJson)
            );
        }
        return JsonUtils.asJsonString(ordersJson);
    }

    private String mapState(OrderState state) {
        switch (state) {
            case SCHEDULED:
                return "todo";
            case PROCESSED:
                return "done";
            default:
                throw new AssertionError("Order with wrong state '" + state + "' selected from database");
        }
    }

}
