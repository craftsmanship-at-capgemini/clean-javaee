package operatorsupport;

import web.Action;
import web.Outcome;
import web.PageController;

/**
 * <p>
 * Currently only navigation back to order list page (associated with
 * {@link OrdersListPageController}) is possible.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@PageController(view = "/operator-break")
public class OperatorBreakPageController {
    
    public static String outcome(String operator) {
        return new Outcome(OperatorBreakPageController.class).
                setParam("operator", operator).build();
    }
    
    private String operator;
    
    @Action
    public String endBreak() {
        // e.g. log to operator performance database
        return OrdersListPageController.outcome();
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
        
    }
}
