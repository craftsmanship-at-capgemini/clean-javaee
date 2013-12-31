package operatorsupport;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

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
     * [
     * {
     * "key": "123456789/A1/2013",
     * "state": "todo",
     * "products": [
     * {"key": "ksr-14-378", "done":0, "qty":1},
     * {"key": "jre-07-666", "done":0, "qty":2}
     * ]
     * },
     * {
     * "key": "564738291/A1/2013",
     * "state": "done",
     * "products": [
     * {"key": "ksr-14-378", "done":1, "qty":1},
     * {"key": "jre-07-666", "done":2, "qty":2}
     * ]
     * }
     * ]
     * </pre>
     *
     */
    @GET
    @Path("/{operator}")
    @Produces("application/json")
    public String getOrders(@PathParam("operator") String operator) {
        String query = ""
                + "select coalesce(cast(array_to_json(array_agg(row_to_json(orders.*))) as varchar), '[]') "
                + "from "
                + "( "
                + "  select "
                + "    o.key||'/'||o.category||'/'||o.year as key, "
                + "    case "
                + "      when orderstate = 'PROCESSED' then 'done' "
                + "      when orderstate = 'SCHEDULED' then 'todo' "
                + "    end as state, "
                + "    ( "
                + "      select array_to_json(array_agg(row_to_json(products.*))) "
                + "      from "
                + "      ("
                + "        select "
                + "          itemkey as key,"
                + "          case "
                + "            when o.orderstate = 'PROCESSED' then quantity "
                + "            when o.orderstate = 'SCHEDULED' then 0 "
                + "          end as done,"
                + "          quantity as qty "
                + "        from orderlines where orderlines.order_id = o.id "
                + "      ) as products "
                + "    ) as products "
                + "  from orders as o join sequenceelements as se "
                + "    on (o.key,o.category,o.year) = (se.key,se.category,se.year) "
                + "  where o.orderState in ('SCHEDULED','PROCESSED') and operator = :operator "
                + "  order by se.sequenceNumber "
                + ") as orders(key, state, products) ";
        String json = entityManager.createNativeQuery(query)
                .setParameter("operator", operator)
                .getSingleResult().toString();
        return json;
    }

}
