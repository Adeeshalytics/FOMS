package com.adeeshalytics.foms.domain;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;

class OrderLifecycleTest {
    private final Instrument instrument = new Instrument("ACME", "Acme Corp", AssetType.EQUITY, "USD", new BigDecimal("0.01"));

    @Test
    void tracksPartialAndFullFillsWithWeightedAveragePrice() {
        Order order = limitBuy(10, "100.00");
        order.accept(Instant.parse("2026-01-01T00:00:00Z"));
        order.applyFill(4, new BigDecimal("99.00"));
        order.applyFill(6, new BigDecimal("101.00"));
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(order.getFilledQuantity()).isEqualTo(10);
        assertThat(order.remainingQuantity()).isZero();
        assertThat(order.getAverageExecutionPrice()).isEqualByComparingTo("100.20000000");
    }

    @Test
    void rejectsInvalidTransitionsAndOverfills() {
        Order order = limitBuy(10, "100.00");
        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
        order.accept(Instant.now());
        assertThatThrownBy(() -> order.applyFill(11, new BigDecimal("100.00"))).isInstanceOf(IllegalArgumentException.class);
        order.cancel();
        assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> order.amend(10, new BigDecimal("101.00"), Instant.now())).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void validatesOrderTypeAndQuantityRules() {
        assertThatThrownBy(() -> new Order("c", "1", instrument, OrderSide.BUY, OrderType.LIMIT, 1, null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Order("c", "2", instrument, OrderSide.BUY, OrderType.MARKET, 1, BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Order("c", "3", instrument, OrderSide.BUY, OrderType.LIMIT, 0, BigDecimal.ONE)).isInstanceOf(IllegalArgumentException.class);
    }

    private Order limitBuy(long quantity, String price) { return new Order("client", "id", instrument, OrderSide.BUY, OrderType.LIMIT, quantity, new BigDecimal(price)); }
}
