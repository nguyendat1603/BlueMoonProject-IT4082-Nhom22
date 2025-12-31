package io.github.ktpm.bluemoonmanagement.service.chat;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import io.github.ktpm.bluemoonmanagement.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ChatService {
    private final ChatMessageRepository repository;

    public ChatService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage saveMessage(String sessionId, String sender, String content) {
        ChatMessage m = new ChatMessage(sessionId, sender, content, Instant.now());
        return repository.save(m);
    }

    public List<ChatMessage> latestMessages() {
        return repository.findTop100ByOrderByCreatedAtDesc();
    }

    public List<ChatMessage> getRecentMessages() {
        List<ChatMessage> list = repository.findTop10ByOrderByCreatedAtDesc();
        if (list == null) {
            System.err.println("SERVER-DEBUG: repository.findTop10ByOrderByCreatedAtDesc() returned null");
        } else {
            System.err.println("SERVER-DEBUG: repository returned " + list.size() + " recent messages");
        }
        return list;
    }

    public List<ChatMessage> messagesForSession(String sessionId) {
        return repository.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }
}


