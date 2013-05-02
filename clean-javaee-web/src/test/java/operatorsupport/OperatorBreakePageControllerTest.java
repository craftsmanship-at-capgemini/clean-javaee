package operatorsupport;

import static org.fest.assertions.api.Assertions.assertThat;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;

import testing.Testing;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class OperatorBreakePageControllerTest {
    
    @Inject OperatorBreakePageController controller;
    
    @Before
    public void setUp() throws Exception {
        Testing.inject(this);
    }
    
    @Test
    public void shouldGoToOrdersListPage() {
        String operator = "Michal";
        
        controller.setOperator(operator);
        String outcome = controller.endBreak();
        
        assertThat(outcome).isEqualTo("/orders-list?faces-redirect=true");
    }
}
