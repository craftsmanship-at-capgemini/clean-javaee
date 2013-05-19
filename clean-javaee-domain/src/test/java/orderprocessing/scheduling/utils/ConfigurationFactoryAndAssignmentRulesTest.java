package orderprocessing.scheduling.utils;

import static orderprocessing.OrderBuilder.anOrder;
import static org.fest.assertions.api.Assertions.assertThat;
import inventory.ItemKey;

import java.lang.reflect.Member;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;

import orderprocessing.OrderEntity;
import orderprocessing.scheduling.AssignmentRule;
import orderprocessing.scheduling.OrderSchedulerService;
import orderprocessing.scheduling.utils.ConfigurationFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
public class ConfigurationFactoryAndAssignmentRulesTest {
    
    Set<AssignmentRule> assignmentRules;
    
    @Before
    public void setUp() throws Exception {
        ConfigurationFactory factory = new ConfigurationFactory();
        factory.properties = factory.createClasspathProperties();
        
        InjectionPoint injectionPointOfRuleSet = createInjectionPoint(OrderSchedulerService.class, "assignmentRules");
        assignmentRules = factory.createAssignmentRules(injectionPointOfRuleSet);
    }
    
    @Test
    public void michalCanPrepareOrdersWithA1Category() {
        // given
        String operator = "michal";
        OrderEntity order = anOrder().likeSomeNew8ROrder().but().
                withOrderKey("0123123123", "A1", "2013").build();
        
        for (AssignmentRule rule : assignmentRules) {
            // when
            boolean canOperatorPrepareOrder = rule.canPrepareOrder(operator, order);
            
            // then
            assertThat(canOperatorPrepareOrder).
                    as("Result of " + rule + " for operator=" + operator + " and order=" + order).
                    isTrue();
        }
    }
    
    @Test
    public void michalCanNotPrepareOrdersWithDifferentCategoryAsA1() {
        // given
        String operator = "michal";
        OrderEntity order = anOrder().likeSomeNew8ROrder().but().
                withOrderKey("0123123123", "C2", "2013").build();
        
        // when
        List<Boolean> ruleResults = new LinkedList<Boolean>();
        for (AssignmentRule rule : assignmentRules) {
            boolean canOperatorPrepareOrder = rule.canPrepareOrder(operator, order);
            ruleResults.add(canOperatorPrepareOrder);
        }
        // then
        assertThat(ruleResults).as("Results of rules " + assignmentRules).contains(false);
    }
    
    @Test
    public void kasiaCanPrepareOrdersWithoutA1CategoryAndTvItems() {
        // given
        String operator = "kasia";
        OrderEntity order = anOrder().likeSomeNew8ROrder().withoutOrderLines().but().
                withOrderKey("0123123123", "C2", "2013").
                withOrderLine(new ItemKey("good-stuff"), 2).build();
        
        for (AssignmentRule rule : assignmentRules) {
            // when
            boolean canOperatorPrepareOrder = rule.canPrepareOrder(operator, order);
            
            // then
            assertThat(canOperatorPrepareOrder).
                    as("Result of " + rule + " for operator=" + operator + " and order=" + order).
                    isTrue();
        }
    }
    
    @Test
    public void kasiaCanNotPrepareOrdersWithA1Category() {
        // given
        String operator = "kasia";
        OrderEntity order = anOrder().likeSomeNew8ROrder().withoutOrderLines().but().
                withOrderKey("0123123123", "A1", "2013").
                withOrderLine(new ItemKey("good-stuff"), 2).build();
        
        // when
        List<Boolean> ruleResults = new LinkedList<Boolean>();
        for (AssignmentRule rule : assignmentRules) {
            boolean canOperatorPrepareOrder = rule.canPrepareOrder(operator, order);
            ruleResults.add(canOperatorPrepareOrder);
        }
        // then
        assertThat(ruleResults).as("Results of rules " + assignmentRules).contains(false);
    }
    
    @Test
    public void kasiaCanNotPrepareOrdersWithTvItems() {
        // given
        String operator = "kasia";
        OrderEntity order = anOrder().likeSomeNew8ROrder().withoutOrderLines().but().
                withOrderKey("0123123123", "X1", "2013").
                withOrderLine(new ItemKey("tv-stuff"), 2).build();
        
        // when
        List<Boolean> ruleResults = new LinkedList<Boolean>();
        for (AssignmentRule rule : assignmentRules) {
            boolean canOperatorPrepareOrder = rule.canPrepareOrder(operator, order);
            ruleResults.add(canOperatorPrepareOrder);
        }
        // then
        assertThat(ruleResults).as("Results of rules " + assignmentRules).contains(false);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private InjectionPoint createInjectionPoint(Class<?> beanClass, String memberName) {
        InjectionPoint injectionPoint = Mockito.mock(InjectionPoint.class);
        Member member = Mockito.mock(Member.class);
        Bean bean = Mockito.mock(Bean.class);
        
        Mockito.when(injectionPoint.getBean()).thenReturn(bean);
        Mockito.when(bean.getBeanClass()).thenReturn(beanClass);
        
        Mockito.when(injectionPoint.getMember()).thenReturn(member);
        Mockito.when(member.getName()).thenReturn(memberName);
        return injectionPoint;
    }
}
