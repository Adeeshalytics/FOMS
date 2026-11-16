package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.AuditEvent;
import java.time.Instant;
import java.util.UUID;

public record AuditEventResponse(UUID id, String eventType, Instant occurredAt, String details) {
    static AuditEventResponse from(AuditEvent event) { return new AuditEventResponse(event.getId(), event.getEventType(), event.getOccurredAt(), event.getDetails()); }
}
