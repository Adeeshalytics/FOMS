package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.*;
import com.adeeshalytics.foms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrderService {
    private final InstrumentRepository instruments;
    private final OrderRepository orders;
    private final ExecutionRepository executions;
    private final AuditEventRepository auditEvents;
    private final InstrumentLockManager locks;
    private final PriceTimeMatcher matcher = new PriceTimeMatcher();

    public OrderService(InstrumentRepository instruments, OrderRepository orders, ExecutionRepository executions,
                        AuditEventRepository auditEvents, InstrumentLockManager locks) {
        this.instruments = instruments; this.orders = orders; this.executions = executions; this.auditEvents = auditEvents; this.locks = locks;
    }

    @Transactional
    public Order submit(SubmitOrderCommand command) {
        Instrument instrument = instruments.findBySymbol(command.symbol()).orElseThrow(() -> new ResourceNotFoundException("instrument not found"));
        if (!instrument.isActive()) throw new IllegalStateException("instrument is inactive");
        ReentrantLock lock = locks.forInstrument(instrument.getId());
        lock.lock();
        try {
            Order incoming = new Order(command.clientId(), command.clientOrderId(), instrument, command.side(), command.orderType(), command.quantity(), command.limitPrice());
            incoming.accept(Instant.now());
            orders.save(incoming);
            auditEvents.save(new AuditEvent(incoming, "ACCEPTED", "{}"));
            List<Order> resting = orders.findActiveByInstrumentId(instrument.getId(), List.of(OrderStatus.ACCEPTED, OrderStatus.PARTIALLY_FILLED));
            for (MatchCandidate match : matcher.match(incoming, resting)) {
                Order passive = match.restingOrder();
                incoming.applyFill(match.quantity(), match.executionPrice());
                passive.applyFill(match.quantity(), match.executionPrice());
                Order buy = incoming.getSide() == OrderSide.BUY ? incoming : passive;
                Order sell = incoming.getSide() == OrderSide.SELL ? incoming : passive;
                executions.save(new Execution(buy, sell, match.quantity(), match.executionPrice(), Instant.now()));
                auditEvents.save(new AuditEvent(incoming, incoming.getStatus() == OrderStatus.FILLED ? "FILLED" : "PARTIALLY_FILLED", "{}"));
                auditEvents.save(new AuditEvent(passive, passive.getStatus() == OrderStatus.FILLED ? "FILLED" : "PARTIALLY_FILLED", "{}"));
            }
            if (incoming.getOrderType() == OrderType.MARKET && incoming.remainingQuantity() > 0) {
                incoming.cancel();
                auditEvents.save(new AuditEvent(incoming, "CANCELLED", "{\"reason\":\"market IOC remainder\"}"));
            }
            return incoming;
        } finally { lock.unlock(); }
    }

    @Transactional(readOnly = true)
    public Order find(java.util.UUID id) { return orders.findById(id).orElseThrow(() -> new ResourceNotFoundException("order not found")); }

    @Transactional(readOnly = true)
    public Order find(String clientId, String clientOrderId) { return orders.findByClientIdAndClientOrderId(clientId, clientOrderId).orElseThrow(() -> new ResourceNotFoundException("order not found")); }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<Order> list(org.springframework.data.domain.Pageable pageable) { return orders.findAll(pageable); }

    @Transactional
    public Order cancel(java.util.UUID orderId) {
        Order order = find(orderId);
        ReentrantLock lock = locks.forInstrument(order.getInstrument().getId()); lock.lock();
        try { order.cancel(); auditEvents.save(new AuditEvent(order, "CANCELLED", "{}")); return order; }
        finally { lock.unlock(); }
    }

    @Transactional
    public Order amend(java.util.UUID orderId, long quantity, java.math.BigDecimal price) {
        Order order = find(orderId);
        ReentrantLock lock = locks.forInstrument(order.getInstrument().getId()); lock.lock();
        try { order.amend(quantity, price, Instant.now()); auditEvents.save(new AuditEvent(order, "AMENDED", "{}")); return order; }
        finally { lock.unlock(); }
    }
}
