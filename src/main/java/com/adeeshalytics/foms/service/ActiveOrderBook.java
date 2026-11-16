package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** In-process read model. It is derived only after a successful database transaction. */
@Component
public class ActiveOrderBook {
    private final Map<UUID, OrderBookSnapshot> snapshots = new ConcurrentHashMap<>();
    public void replace(UUID instrumentId, Collection<Order> orders) { snapshots.put(instrumentId, OrderBookSnapshot.from(orders)); }
    public Optional<OrderBookSnapshot> find(UUID instrumentId) { return Optional.ofNullable(snapshots.get(instrumentId)); }
    public int size() { return snapshots.size(); }
}
