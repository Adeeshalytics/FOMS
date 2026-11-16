package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;
import com.adeeshalytics.foms.domain.OrderStatus;
import com.adeeshalytics.foms.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import java.util.*;

/** Recreates the read model from PostgreSQL at startup and after committed changes. */
@Component
public class OrderBookRecovery {
    private static final Logger log = LoggerFactory.getLogger(OrderBookRecovery.class);
    private static final List<OrderStatus> ACTIVE = List.of(OrderStatus.ACCEPTED, OrderStatus.PARTIALLY_FILLED);
    private final OrderRepository orders;
    private final ActiveOrderBook book;
    public OrderBookRecovery(OrderRepository orders, ActiveOrderBook book) { this.orders = orders; this.book = book; }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void reloadAll() {
        Map<UUID, List<Order>> byInstrument = new HashMap<>();
        for (Order order : orders.findAllActive(ACTIVE)) byInstrument.computeIfAbsent(order.getInstrument().getId(), ignored -> new ArrayList<>()).add(order);
        byInstrument.forEach(book::replace);
        log.info("Rebuilt {} active order-book snapshots from PostgreSQL", byInstrument.size());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public void refreshAfterCommit(OrderBookRefreshRequested event) {
        book.replace(event.instrumentId(), orders.findActiveByInstrumentId(event.instrumentId(), ACTIVE));
    }
}
