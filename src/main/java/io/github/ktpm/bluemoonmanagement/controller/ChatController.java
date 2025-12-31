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
}


