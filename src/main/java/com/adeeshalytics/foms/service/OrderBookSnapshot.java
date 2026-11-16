package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;
import com.adeeshalytics.foms.domain.OrderSide;
import java.math.BigDecimal;
import java.util.*;

/** Immutable, persisted-order-derived view. Market orders never rest in this book. */
public record OrderBookSnapshot(List<OrderBookLevel> bids, List<OrderBookLevel> asks) {
    public static OrderBookSnapshot from(Collection<Order> activeOrders) {
        return new OrderBookSnapshot(levels(activeOrders, OrderSide.BUY, Comparator.reverseOrder()), levels(activeOrders, OrderSide.SELL, Comparator.naturalOrder()));
    }
    private static List<OrderBookLevel> levels(Collection<Order> orders, OrderSide side, Comparator<BigDecimal> comparator) {
        Map<BigDecimal, long[]> levels = new TreeMap<>(comparator);
        orders.stream().filter(order -> order.getSide() == side && order.getLimitPrice() != null)
                .forEach(order -> { long[] aggregate = levels.computeIfAbsent(order.getLimitPrice(), ignored -> new long[2]); aggregate[0] += order.remainingQuantity(); aggregate[1]++; });
        return levels.entrySet().stream().map(entry -> new OrderBookLevel(entry.getKey(), entry.getValue()[0], Math.toIntExact(entry.getValue()[1]))).toList();
    }
}
