package io.github.ktpm.bluemoonmanagement.controller;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import io.github.ktpm.bluemoonmanagement.service.chat.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage message) {
        // persist and return
        chatService.saveMessage(message.getSessionId(), message.getSender(), message.getContent());
        return message;
    }

    /**
     * Return recent chat history (up to 10) when client requests via STOMP.
     * Client should send to /app/chat.history; server will broadcast the list to /topic/history.
     */
    @MessageMapping("/chat.history")
    @SendTo("/topic/history")
    public java.util.List<ChatMessage> history() {
        System.err.println("SERVER-DEBUG: /app/chat.history requested");
        java.util.List<ChatMessage> recent = chatService.getRecentMessages();
        if (recent == null) {
            System.err.println("SERVER-DEBUG: chatService.getRecentMessages() returned null");
            return java.util.Collections.emptyList();
        }
        System.err.println("SERVER-DEBUG: chatService returned size=" + recent.size());
        // current service returns newest-first; reverse to send oldest-first
        java.util.Collections.reverse(recent);
        System.err.println("SERVER-DEBUG: sending history size=" + recent.size());
        return recent;
    }

@MessageMapping("/chat.ping")
public void ping() {
    // Simple ping endpoint for heartbeat monitoring
    // No response needed, just confirms connection is alive
}

@MessageMapping("/chat.test")
@SendTo("/topic/public")
public io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage testMessage() {
    System.out.println("CHAT: [SERVER] Test message triggered from client");
    io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage testMsg =
        new io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage(
            "test-session", "SERVER", "[TEST MESSAGE] Server broadcast test", java.time.Instant.now()
        );
    chatService.saveMessage(testMsg.getSessionId(), testMsg.getSender(), testMsg.getContent());
    System.out.println("CHAT: [SERVER] Test message saved and about to broadcast");
    return testMsg;
}
}


