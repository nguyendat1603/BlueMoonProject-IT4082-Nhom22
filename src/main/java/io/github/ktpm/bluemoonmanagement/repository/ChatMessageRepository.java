package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop100ByOrderByCreatedAtDesc();
    List<ChatMessage> findTop10ByOrderByCreatedAtDesc();
    List<ChatMessage> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}


