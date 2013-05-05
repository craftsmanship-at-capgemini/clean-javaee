package orderprocessing.scheduling;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import orderprocessing.OrderEntity;
import server.Configuration;

@ApplicationScoped
public class ConfigurationFactory {
    
    @Inject @Configuration Properties properties;
    
    @Produces
    @Configuration
    String createString(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() +
                injectionPoint.getMember().getName();
        return properties.getProperty(key);
    }
    
    @Produces
    @Configuration
    Set<String> createSet(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() +
                injectionPoint.getMember().getName();
        Set<String> set = new HashSet<String>(
                Arrays.asList(properties.getProperty(key).split(",")));
        return set;
    }
    
    @Produces
    @Configuration
    Set<AssignmentRule> createAssignmentRules(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() +
                injectionPoint.getMember().getName();
        Set<AssignmentRule> ruleset = new HashSet<AssignmentRule>();
        String rulesString = properties.getProperty(key);
        for (String ruleLine : rulesString.split("\n")) {
            String[] ruleParts = ruleLine.split(":");
            if (ruleParts.length == 2) {
                ruleset.add(createRule(ruleParts[0], ruleParts[1]));
            } else {
                // log business rule error
            }
        }
        
        return ruleset;
    }
    
    AssignmentRule createRule(final String ruleOperator, final String ruleDefinition) {
        return new AssignmentRule() {
            @Override
            public boolean canPrepareOrder(String operator, OrderEntity order) {
                if (operator.equals(ruleOperator.trim())) {
                    // try {
                    Binding binding = new Binding();
                    binding.setVariable("order", order);
                    GroovyShell rule = new GroovyShell(binding);
                    Object value = rule.evaluate(ruleDefinition);
                    return (Boolean) value;
                    // } catch (GroovyRuntimeException e) {
                    // log business rule error
                    // return true;
                    // }
                } else {
                    return true;
                }
            }
        };
    }
    
    @Produces
    @Configuration
    Properties createClasspathProperties() {
        String file = "/orderprocessing.scheduling.Configuration.properties";
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
