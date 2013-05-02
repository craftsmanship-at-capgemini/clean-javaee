package operatorsupport;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@Named("messages")
@RequestScoped
public class OperatorsupportI18n {
    
    private ResourceBundle bundle;
    
    @Inject
    public OperatorsupportI18n(ResourceBundle bundle) {
        this.bundle = bundle;
    }
    
    public String operatorBreakePageTitle(String operator) {
        return MessageFormat.format(bundle.getString("operatorBreakePageTitle"), operator);
    }
    
    public String ordersListPageTitle(String operator) {
        return MessageFormat.format(bundle.getString("ordersListPageTitle"), operator);
    }
    
    public FacesMessage cookieNotDefinedMessage(Severity severity, String operatorKeyCookie) {
        return new FacesMessage(
                severity,
                MessageFormat.format(bundle.getString("cookieNotDefinedMessage"), operatorKeyCookie),
                MessageFormat.format(bundle.getString("cookieNotDefinedMessage_detail"), operatorKeyCookie));
    }
}
