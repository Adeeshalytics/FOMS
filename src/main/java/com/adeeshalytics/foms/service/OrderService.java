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
        Instrument instrument = instruments.findBySymbol(command.symbol()).orElseThrow(() -> new IllegalArgumentException("instrument not found"));
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
}
