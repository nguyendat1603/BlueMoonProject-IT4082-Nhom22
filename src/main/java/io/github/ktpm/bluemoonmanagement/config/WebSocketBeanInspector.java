package io.github.ktpm.bluemoonmanagement.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class WebSocketBeanInspector implements BeanFactoryPostProcessor {
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            String[] all = beanFactory.getBeanDefinitionNames();
            System.out.println("=== WebSocketBeanInspector: scanning bean definitions ===");
            Arrays.stream(all)
                .filter(n -> n.toLowerCase().contains("websocket") || n.toLowerCase().contains("stomp") || n.toLowerCase().contains("subprotocol") || n.toLowerCase().contains("messagebroker"))
                .forEach(n -> System.out.println("WS bean def: " + n));
            // Also print specific candidate beans if present
            if (beanFactory.containsBeanDefinition("webSocketMessageBrokerHandler")) {
                System.out.println("Found webSocketMessageBrokerHandler bean definition");
            }
        } catch (Exception e) {
            System.err.println("WebSocketBeanInspector error: " + e.getMessage());
        }
    }
}


