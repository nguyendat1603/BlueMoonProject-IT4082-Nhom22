package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    private String sender;
    private String content;
    private Instant createdAt;

    public ChatMessage() {}

    public ChatMessage(String sessionId, String sender, String content, Instant createdAt) {
        this.sessionId = sessionId;
        this.sender = sender;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}


