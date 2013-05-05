package orderprocessing.scheduling;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import orderprocessing.OrderEntity;
import orderprocessing.OrderLineEntity;

@ApplicationScoped
public class ConfigurationFactory {
    
    Properties properties;
    
    public ConfigurationFactory() {
        properties = readClasspathProperties("/orderprocessing.scheduling.Configuration.properties");
    }
    
    @Produces
    @server.Configuration
    String createString(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() +
                injectionPoint.getMember().getName();
        return properties.getProperty(key);
    }
    
    @Produces
    @server.Configuration
    Set<String> createSet(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() +
                injectionPoint.getMember().getName();
        Set<String> set = new HashSet<String>(
                Arrays.asList(properties.getProperty(key).split(",")));
        return set;
    }
    
    @Produces
    @server.Configuration
    Set<AssignmentRule> createAssignmentRules() {
        return new HashSet<AssignmentRule>(Arrays.asList(
                new AssignmentRule() {
                    @Override
                    public boolean canPrepareOrder(String operator, OrderEntity order) {
                        if (operator.equals("michal")) {
                            return order.getOrderKey().getCategory().equals("A1");
                        }
                        return true;
                    }
                },
                new AssignmentRule() {
                    @Override
                    public boolean canPrepareOrder(String operator, OrderEntity order) {
                        if (operator.equals("kasia")) {
                            if (order.getOrderKey().getCategory().equals("A1")) {
                                return false;
                            }
                            Set<OrderLineEntity> orderLines = order.getOrderLines();
                            for (OrderLineEntity orderLine : orderLines) {
                                if (orderLine.getItemKey().isLike("tv*")) {
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
                ));
    }
    
    @Produces
    @server.Configuration
    ProcessingCostCalculator createProcessingCostCalculator() {
        return null;
    }
    
    private Properties readClasspathProperties(String file) {
        Properties properties = new Properties();
        try {
            InputStream input =
                    getClass().getResourceAsStream(file);
            properties.load(input);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return properties;
    }
    
}
