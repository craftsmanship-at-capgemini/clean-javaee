package operatorsupport;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.eq;
import static testing.Conditions.equalFacesMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.inject.Inject;

import orderprocessing.OrderKey;
import orderprocessing.OrderProgressManagementRemote;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import testing.Testing;
import testing.data.ResourceBundleWithAnyKey;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OrdersListPageControllerTest {
    
    @Inject OrdersListPageController controller;
    
    @Mock OrderProgressManagementRemote orderProgressManagement;
    @Mock OperatorTasksRemote operatorTasksProvider;
    
    @Inject Collection<FacesMessage> messages = new HashSet<FacesMessage>();
    @Inject OperatorsupportI18n i18n = new OperatorsupportI18n(new ResourceBundleWithAnyKey());
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldDisplayEmptyListAndShowErrorMessageWhenCookieIsNotDefined() {
        
        List<OrderKey> displayedOrders = controller.getOrders();
        
        assertThat(displayedOrders).isEmpty();
        assertThat(messages).hasSize(1).
                haveExactly(1, equalFacesMessage("cookieNotDefinedMessage", FacesMessage.SEVERITY_ERROR));
    }
    
    @Test
    public void shouldDisplayOrdersAssignedToMichal() {
        String operator = "Michal";
        List<OrderKey> expected = Arrays.asList(
                new OrderKey("123456789", "H2", "2013"),
                new OrderKey("987654321", "A3", "2013")
                );
        Mockito.when(operatorTasksProvider.getOrderSequence(operator)).thenReturn(expected);
        
        controller.setOperator(operator);
        List<OrderKey> displayedOrders = controller.getOrders();
        
        assertThat(displayedOrders).isEqualTo(expected);
    }
    
    @Test
    public void shouldDisplayOnlyOrdersAssignedToMichal() {
        String operator = "Michal";
        List<OrderKey> expected = Arrays.asList(
                new OrderKey("123456789", "H2", "2013"),
                new OrderKey("987654321", "A3", "2013")
                );
        List<OrderKey> notExpected = Arrays.asList(
                new OrderKey("000000000", "X1", "2013")
                );
        
        Mockito.when(operatorTasksProvider.getOrderSequence(not(eq(operator)))).thenReturn(notExpected);
        Mockito.when(operatorTasksProvider.getOrderSequence(operator)).thenReturn(expected);
        
        controller.setOperator(operator);
        List<OrderKey> displayedOrders = controller.getOrders();
        
        assertThat(displayedOrders).isEqualTo(expected);
    }
    
    @Test
    public void shouldGoToOperatorBrakePage() {
        String operator = "Michal";
        
        controller.setOperator(operator);
        String outcome = controller.startBreak();
        
        assertThat(outcome).isEqualTo("/operator-break?faces-redirect=true&amp; operator=Michal");
    }
    
    @Test
    public void shouldStayOnPageIfOperatorNotDefined() {
        
        controller.setOperator(null);
        String outcome = controller.startBreak();
        
        assertThat(outcome).isNull();
    }
    
    @Test
    public void shouldMarkOrderAsProcessedWhenOrderDoneActionIsCalled() {
        OrderKey orderKey = new OrderKey("123456789", "H2", "2013");
        
        controller.orderDone(orderKey);
        
        Mockito.verify(orderProgressManagement).orderDone(orderKey);
    }
}
