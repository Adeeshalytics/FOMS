package com.adeeshalytics.foms.repository;

import com.adeeshalytics.foms.domain.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;

public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
    List<AuditEvent> findByOrderIdOrderByOccurredAtAsc(UUID orderId);
}
