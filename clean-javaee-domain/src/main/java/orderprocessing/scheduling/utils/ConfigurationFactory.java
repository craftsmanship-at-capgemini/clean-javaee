package orderprocessing.scheduling.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyRuntimeException;
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
import orderprocessing.scheduling.AssignmentRule;
import server.Configuration;

/**
 * Produces and injects configuration from property file.
 * 
 * @author Michal Michaluk <michaluk.michal@gmail.com>
 */
@ApplicationScoped
public class ConfigurationFactory {
    
    @Inject @Configuration Properties properties;
    
    @Produces
    @Configuration
    String createString(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() + "." +
                injectionPoint.getMember().getName();
        return properties.getProperty(key);
    }
    
    @Produces
    @Configuration
    Set<String> createSet(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() + "." +
                injectionPoint.getMember().getName();
        Set<String> set = new HashSet<String>(
                Arrays.asList(properties.getProperty(key).split(",")));
        return set;
    }
    
    @Produces
    @Configuration
    Set<AssignmentRule> createAssignmentRules(InjectionPoint injectionPoint) {
        String key = injectionPoint.getBean().getBeanClass().getName() + "." +
                injectionPoint.getMember().getName();
        Set<AssignmentRule> ruleset = new HashSet<AssignmentRule>();
        String rulesString = properties.getProperty(key);
        String[] rules = rulesString.split(";");
        for (String ruleLine : rules) {
            String[] ruleParts = ruleLine.split(":");
            if (ruleParts.length == 2) {
                ruleset.add(createRule(ruleParts[0].trim(), ruleParts[1].trim()));
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
                    try {
                        Binding binding = new Binding();
                        binding.setVariable("order", order);
                        GroovyShell rule = new GroovyShell(binding);
                        Object value = rule.evaluate(ruleDefinition);
                        return (Boolean) value;
                    } catch (GroovyRuntimeException e) {
                        // log business rule error
                        return true;
                    }
                } else {
                    return true;
                }
            }
            
            @Override
            public String toString() {
                return "GroovyAssignmentRule [operator=" + ruleOperator + ", definition=<" + ruleDefinition + ">]";
            }
        };
    }
    
    @Produces
    @Configuration
    static Properties createClasspathProperties() {
        String file = "/orderprocessing.scheduling.utils.ConfigurationFactory.properties";
        Properties properties = new Properties();
        try {
            InputStream input =
                ConfigurationFactory.class.getResourceAsStream(file);
            properties.load(input);
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return properties;
    }
    
}
