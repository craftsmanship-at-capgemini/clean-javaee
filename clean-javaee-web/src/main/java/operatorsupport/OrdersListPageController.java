package operatorsupport;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.Cookie;

import orderprocessing.OrderKey;
import orderprocessing.OrderProgressManagementRemote;
import web.Action;
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
    
    @EJB OrderProgressManagementRemote orderProgressManagement;
    @EJB OperatorTasksRemote operatorTasks;
    
    private List<OrderKey> orders;
    
    public List<OrderKey> getOrders() {
        if (orders == null) {
            // get cookie
            Map<String, Object> cookieMap = FacesContext
                    .getCurrentInstance().getExternalContext()
                    .getRequestCookieMap();
            Cookie cookie = (Cookie) cookieMap.get(OPERATOR_KEY_COOKIE);
            String operator = cookie.getValue();
            // if cooky defined
            if (operator != null && !operator.isEmpty()) {
                orders = operatorTasks.getOrderSequence(operator);
            } else {
                // get localized message
                ResourceBundle bundle = FacesContext.getCurrentInstance().
                        getApplication().getResourceBundle(FacesContext.getCurrentInstance(), "messagesBundle");
                FacesMessage message = new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        MessageFormat.format(bundle.getString("cookieNotDefinedMessage"), OPERATOR_KEY_COOKIE),
                        MessageFormat.format(bundle.getString("cookieNotDefinedMessage_detail"), OPERATOR_KEY_COOKIE));
                // add message
                FacesContext.getCurrentInstance().addMessage(null, message);
                orders = Collections.emptyList();
            }
        }
        return orders;
    }
    
    @Action
    public void orderDone(OrderKey orderKey) {
        orderProgressManagement.orderDone(orderKey);
    }
    
    @Action
    public String startBreak() {
        Map<String, Object> cookieMap = FacesContext
                .getCurrentInstance().getExternalContext()
                .getRequestCookieMap();
        Cookie cookie = (Cookie) cookieMap.get(OPERATOR_KEY_COOKIE);
        String operator = cookie.getValue();
        if (operator != null && !operator.isEmpty()) {
            // build outcome
            try {
                return "/operator-break?faces-redirect=true&amp; operator=" + URLEncoder.encode(operator, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // "UTF-8" is supported
                throw new AssertionError(e);
            }
        } else {
            // stay on page
            return null;
        }
    }
    
}
