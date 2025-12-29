package io.github.ktpm.bluemoonmanagement.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component
public class WebSocketHandlerDumper implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String name = "stompWebSocketHandlerMapping";
            if (!event.getApplicationContext().containsBean(name)) {
                System.out.println("WebSocketHandlerDumper: bean not present: " + name);
                return;
            }
            Object bean = event.getApplicationContext().getBean(name);
            System.out.println("WebSocketHandlerDumper: bean class=" + bean.getClass().getName());
            // Try to find a method that returns Map and invoke it
            for (Method m : bean.getClass().getMethods()) {
                if ((m.getName().equals("getHandlerMap") || m.getName().toLowerCase().contains("handler")) && Map.class.isAssignableFrom(m.getReturnType()) && m.getParameterCount() == 0) {
                    try {
                        Object res = m.invoke(bean);
                        if (res instanceof Map) {
                            Map<?,?> map = (Map<?,?>) res;
                            System.out.println("WebSocketHandlerDumper: found handler map via " + m.getName() + " size=" + map.size());
                            map.keySet().stream().limit(20).forEach(k -> System.out.println("  key=" + k + " -> " + map.get(k)));
                        }
                    } catch (Exception e) {
                        System.out.println("WebSocketHandlerDumper: invoking " + m.getName() + " failed: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("WebSocketHandlerDumper error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


