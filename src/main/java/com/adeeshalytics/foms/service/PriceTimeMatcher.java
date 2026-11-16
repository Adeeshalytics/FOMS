package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;
import com.adeeshalytics.foms.domain.OrderSide;
import com.adeeshalytics.foms.domain.OrderType;
import java.util.*;

/** Stateless matcher: callers persist all mutations in one transaction. */
public final class PriceTimeMatcher {
    public List<MatchCandidate> match(Order incoming, Collection<Order> restingOrders) {
        Comparator<Order> priority = incoming.getSide() == OrderSide.BUY
                ? Comparator.comparing(Order::getLimitPrice).thenComparing(Order::getAcceptedAt)
                : Comparator.comparing(Order::getLimitPrice, Comparator.reverseOrder()).thenComparing(Order::getAcceptedAt);
        List<Order> candidates = restingOrders.stream()
                .filter(o -> o != incoming)
                .filter(o -> o.getSide() != incoming.getSide())
                .sorted(priority).toList();
        Map<Order, Long> reserved = new HashMap<>();
        long incomingLeaves = incoming.remainingQuantity();
        List<MatchCandidate> matches = new ArrayList<>();
        for (Order resting : candidates) {
            if (incomingLeaves == 0 || !crosses(incoming, resting)) break;
            // Cancel/stop at the best self order. Matching beyond it would bypass price-time priority.
            if (resting.getClientId().equals(incoming.getClientId())) break;
            long restingLeaves = resting.remainingQuantity() - reserved.getOrDefault(resting, 0L);
            long quantity = Math.min(incomingLeaves, restingLeaves);
            if (quantity <= 0) continue;
            matches.add(new MatchCandidate(resting, quantity, resting.getLimitPrice()));
            reserved.merge(resting, quantity, Long::sum);
            incomingLeaves -= quantity;
        }
        return List.copyOf(matches);
    }

    private boolean crosses(Order incoming, Order resting) {
        if (incoming.getOrderType() == OrderType.MARKET) return true;
        return incoming.getSide() == OrderSide.BUY
                ? incoming.getLimitPrice().compareTo(resting.getLimitPrice()) >= 0
                : incoming.getLimitPrice().compareTo(resting.getLimitPrice()) <= 0;
    }
}
