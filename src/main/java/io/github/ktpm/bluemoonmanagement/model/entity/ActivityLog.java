package io.github.ktpm.bluemoonmanagement.model.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "activity_log")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;
    @Column(name = "user_name")
    private String user;
    private Instant timestamp;
    private String actionDescription;
    private String actionType;

    public ActivityLog() {}

    public ActivityLog(String sessionId, String user, Instant timestamp, String actionDescription, String actionType) {
        this.sessionId = sessionId;
        this.user = user;
        this.timestamp = timestamp;
        this.actionDescription = actionDescription;
        this.actionType = actionType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getActionDescription() { return actionDescription; }
    public void setActionDescription(String actionDescription) { this.actionDescription = actionDescription; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
}
