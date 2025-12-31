package io.github.ktpm.bluemoonmanagement.controller;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import io.github.ktpm.bluemoonmanagement.service.chat.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {
    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> send(@RequestParam String sessionId, @RequestParam String sender, @RequestParam String content) {
        ChatMessage m = chatService.saveMessage(sessionId, sender, content);
        return ResponseEntity.ok(m);
    }

    @GetMapping("/latest")
    public ResponseEntity<List<ChatMessage>> latest() {
        return ResponseEntity.ok(chatService.latestMessages());
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<ChatMessage>> bySession(@PathVariable String sessionId) {
        return ResponseEntity.ok(chatService.messagesForSession(sessionId));
    }
}


