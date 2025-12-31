package io.github.ktpm.bluemoonmanagement.config;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import io.github.ktpm.bluemoonmanagement.service.chat.ChatService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

@Component
public class ChatSubscriptionListener {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatSubscriptionListener(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        try {
            StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
            String dest = sha.getDestination();
            if (dest == null) return;
            if (dest.equals("/topic/history")) {
                System.err.println("SERVER-DEBUG: subscription to /topic/history detected, preparing history");
                List<ChatMessage> recent = chatService.getRecentMessages();
                if (recent == null || recent.isEmpty()) {
                    System.err.println("SERVER-DEBUG: no recent messages to send for history");
                } else {
                    java.util.Collections.reverse(recent);
                    System.err.println("SERVER-DEBUG: publishing history of size=" + recent.size() + " to /topic/history");
                    messagingTemplate.convertAndSend("/topic/history", recent.toArray(new ChatMessage[0]));
                }
            }
        } catch (Exception e) {
            System.err.println("SERVER-DEBUG: error in subscription listener: " + e.getMessage());
            e.printStackTrace();
        }
    }
}


