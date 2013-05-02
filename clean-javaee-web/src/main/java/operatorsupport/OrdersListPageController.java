package operatorsupport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;

import orderprocessing.OrderKey;
import orderprocessing.scheduling.OrderSchedulerRemote;
import web.Action;
import web.CookieParam;
import web.Messages;
import web.Outcome;
import web.PageController;

/**
 * <p>
 * Page displays list of orders for particular operator. Operator needs to by defined as cookie on
 * client site.
 * <p>
 * Page allows lock, unlock and mark as done an order.
 * <p>
 * Navigation to brake page (associated with {@link OperatorBreakePageController}) is possible.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@PageController(view = "/orders-list")
public class OrdersListPageController {
    
    private static final String OPERATOR_KEY_COOKIE = "operatorKey";
    
    public static String outcome() {
        return new Outcome(OrdersListPageController.class).build();
    }
    
    @EJB private OrderSchedulerRemote orderScheduler;
    
    @Inject @Messages private Collection<FacesMessage> messages;
    @Inject private OperatorsupportI18n i18n;
    
    private String operator;
    private List<OrderKey> orders;
    
    public List<OrderKey> getOrders() {
        if (orders == null) {
            if (isOperatorCookieDefined()) {
                orders = orderScheduler.getOrderSequence(operator);
            } else {
                FacesMessage message = i18n.cookieNotDefinedMessage(
                        FacesMessage.SEVERITY_ERROR,
                        OPERATOR_KEY_COOKIE);
                messages.add(message);
                orders = Collections.emptyList();
            }
        }
        return orders;
    }
    
    public boolean isOperatorCookieDefined() {
        return !(operator == null || operator.isEmpty());
    }
    
    public String getOperator() {
        return operator;
    }
    
    @Inject
    @CookieParam(OPERATOR_KEY_COOKIE)
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    @Action
    public void lockOrder(OrderKey orderKey) {
        orderScheduler.lockOrder(orderKey);
    }
    
    @Action
    public void unlockOrder(OrderKey orderKey) {
        orderScheduler.unlockOrder(orderKey);
    }
    
    @Action
    public void orderDone(OrderKey orderKey) {
        orderScheduler.markOrderAsProcessed(orderKey);
    }
    
    @Action
    public String startBreak() {
        if (isOperatorCookieDefined()) {
            // e.g. log to operator performance database
            return OperatorBreakePageController.outcome(operator);
        } else {
            return Outcome.stayOnPage();
        }
    }
    
}
