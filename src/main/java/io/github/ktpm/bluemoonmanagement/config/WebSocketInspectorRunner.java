package io.github.ktpm.bluemoonmanagement.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

@Component
public class WebSocketInspectorRunner implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String[] candidates = new String[] {
                "stompWebSocketHandlerMapping",
                "subProtocolWebSocketHandler",
                "stompBrokerRelayMessageHandler",
                "webSocketMessageBrokerStats",
                "stompMessageHandler",
            };

            System.out.println("=== WebSocketInspectorRunner: inspecting beans after context refresh ===");
            for (String name : candidates) {
                try {
                    if (event.getApplicationContext().containsBean(name)) {
                        Object bean = event.getApplicationContext().getBean(name);
                        System.out.println("Bean: " + name + " -> " + bean.getClass().getName());
                        inspectBeanFields(bean);
                    } else {
                        System.out.println("Bean not present: " + name);
                    }
                } catch (Exception e) {
                    System.out.println("Error inspecting bean " + name + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("WebSocketInspectorRunner failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void inspectBeanFields(Object bean) {
        Class<?> cls = bean.getClass();
        for (Field f : cls.getDeclaredFields()) {
            f.setAccessible(true);
            try {
                Object val = f.get(bean);
                if (val == null) continue;
                if (val instanceof Map) {
                    System.out.println("  Field " + f.getName() + " (Map) size=" + ((Map<?, ?>) val).size());
                } else if (val instanceof Collection) {
                    System.out.println("  Field " + f.getName() + " (Collection) size=" + ((Collection<?>) val).size());
                } else {
                    // print type
                    System.out.println("  Field " + f.getName() + " -> " + val.getClass().getName());
                }
            } catch (Exception e) {
                System.out.println("  Field " + f.getName() + " -> (error) " + e.getMessage());
            }
        }
    }
}


