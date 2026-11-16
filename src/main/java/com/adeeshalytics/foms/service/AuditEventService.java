package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.AuditEvent;
import com.adeeshalytics.foms.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class AuditEventService {
    private final AuditEventRepository events;
    public AuditEventService(AuditEventRepository events) { this.events = events; }
    @Transactional(readOnly = true) public List<AuditEvent> forOrder(UUID orderId) { return events.findByOrderIdOrderByOccurredAtAsc(orderId); }
}
