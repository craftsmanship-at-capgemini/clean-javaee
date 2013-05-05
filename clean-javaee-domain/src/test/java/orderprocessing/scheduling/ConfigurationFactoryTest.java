package orderprocessing.scheduling;

import inventory.ItemKey;

import java.util.Properties;

import orderprocessing.OrderBuilder;
import orderprocessing.OrderEntity;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ConfigurationFactoryTest {
    
    ConfigurationFactory factory = new ConfigurationFactory();
    
    @Before
    public void setUp() throws Exception {
    }
    
    ItemKey itemKeyNotProcessedByKasia = new ItemKey("tv-sams-54003-42");
    
    @Test
    public void michalCanPrepareOnlyA1Category() {
        factory.properties = new Properties();
        AssignmentRule rule = factory.createRule("michal", "order.orderKey.category == 'A1'");
        
        OrderEntity orderA2 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("0123123123", "A2", "2013").build();
        OrderEntity orderA1 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("0123123123", "A1", "2013").build();
        
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderA1));
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderA2));
        Assert.assertTrue(rule.canPrepareOrder("michal", orderA1));
        Assert.assertFalse(rule.canPrepareOrder("michal", orderA2));
    }
    
    @Test
    public void kasiaCantPrepareA1Category() {
        factory.properties = new Properties();
        AssignmentRule rule = factory.createRule("kasia", "order.orderKey.category != 'A1'");
        
        OrderEntity orderA2 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("0123123123", "A2", "2013").build();
        OrderEntity orderA1 = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderKey("0123123123", "A1", "2013").build();
        
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderA1));
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderA2));
        Assert.assertFalse(rule.canPrepareOrder("kasia", orderA1));
        Assert.assertTrue(rule.canPrepareOrder("kasia", orderA2));
    }
    
    @Test
    public void kasiaCantPrepareOrdersWithTVItems() {
        factory.properties = new Properties();
        AssignmentRule rule = factory.createRule("kasia", "! order.orderLines.any{ it.itemKey.itemKey ==~ 'tv.*' }");
        
        OrderEntity orderWithTV = OrderBuilder.anOrder().likeSomeNew8ROrder().
                withOrderLine(new ItemKey("tv-sam"), 1).build();
        OrderEntity orderWithoutTV = OrderBuilder.anOrder().likeSomeNew8ROrder().build();
        
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderWithTV));
        Assert.assertTrue(rule.canPrepareOrder("łukasz", orderWithoutTV));
        Assert.assertFalse(rule.canPrepareOrder("kasia", orderWithTV));
        Assert.assertTrue(rule.canPrepareOrder("kasia", orderWithoutTV));
    }
}
