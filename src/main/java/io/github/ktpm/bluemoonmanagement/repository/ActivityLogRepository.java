package io.github.ktpm.bluemoonmanagement.repository;

import io.github.ktpm.bluemoonmanagement.model.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findBySessionIdOrderByTimestampAsc(String sessionId);
    List<ActivityLog> findTop100ByOrderByTimestampDesc();
}
