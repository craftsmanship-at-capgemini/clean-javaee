package operatorsupport;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;

import orderprocessing.OrderKey;
import orderprocessing.OrderProgressManagementRemote;
import web.Action;
import web.CookieParam;
import web.Messages;
import web.Outcome;
import web.PageController;

/**
 * <p>
 * Page displays list of orders for particular operator.
 * <p>
 * <strong>Note</strong> Operator needs to by defined as cookie on client site.
 * <p>
 * Page allows to mark an order as done or navigate to brake page (associated
 * with {@link OperatorBreakPageController}).
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@PageController(view = "/orders-list")
public class OrdersListPageController {
    
    private static final String OPERATOR_KEY_COOKIE = "operatorKey";
    
    public static String outcome() {
        return new Outcome(OrdersListPageController.class).build();
    }
    
    @EJB OrderProgressManagementRemote orderProgressManagement;
    @EJB OperatorTasksRemote operatorTasks;
    
    @Inject @Messages Collection<FacesMessage> messages;
    @Inject OperatorsupportI18n i18n;
    
    private String operator;
    private List<OrderKey> orders;
    
    public List<OrderKey> getOrders() {
        if (orders == null) {
            if (isOperatorCookieDefined()) {
                orders = operatorTasks.getOrderSequence(operator);
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
    public void setOperator(@CookieParam(OPERATOR_KEY_COOKIE) String operator) {
        this.operator = operator;
    }
    
    @Action
    public void orderDone(OrderKey orderKey) {
        orderProgressManagement.orderDone(orderKey);
    }
    
    @Action
    public String startBreak() {
        if (isOperatorCookieDefined()) {
            // e.g. log to operator performance database
            return OperatorBreakPageController.outcome(operator);
        } else {
            return Outcome.stayOnPage();
        }
    }
    
}
