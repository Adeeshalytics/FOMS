package com.adeeshalytics.foms.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "order_id") private Order order;
    @Column(name = "event_type", nullable = false) private String eventType;
    @Column(name = "occurred_at", nullable = false) private Instant occurredAt;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb") private String details;
    protected AuditEvent() { }
    public AuditEvent(Order order, String eventType, String details) { this.order = order; this.eventType = eventType; this.details = details; this.occurredAt = Instant.now(); }
    public UUID getId() { return id; }
    public Order getOrder() { return order; }
    public String getEventType() { return eventType; }
    public Instant getOccurredAt() { return occurredAt; }
    public String getDetails() { return details; }
}
