package io.github.ktpm.bluemoonmanagement.service.activityLog;

import io.github.ktpm.bluemoonmanagement.model.entity.ActivityLog;
import io.github.ktpm.bluemoonmanagement.repository.ActivityLogRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class ActivityLogService {
    private final ActivityLogRepository repository;

    public ActivityLogService(ActivityLogRepository repository) {
        this.repository = repository;
    }

    public ActivityLog saveActivity(String sessionId, String user, String actionDescription, String actionType) {
        ActivityLog activity = new ActivityLog(sessionId, user, Instant.now(), actionDescription, actionType);
        return repository.save(activity);
    }

    public List<ActivityLog> getAllActivities() {
        return repository.findTop100ByOrderByTimestampDesc();
    }

    public List<ActivityLog> getActivitiesBySession(String sessionId) {
        return repository.findBySessionIdOrderByTimestampAsc(sessionId);
    }
}
