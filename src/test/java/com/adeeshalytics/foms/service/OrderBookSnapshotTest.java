package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class OrderBookSnapshotTest {
    private final Instrument instrument = new Instrument("ACME", "Acme", AssetType.EQUITY, "USD", new BigDecimal("0.01"));

    @Test
    void aggregatesLiquidityAndOrdersAtEachPriceInDisplayPriority() {
        Order bid100a = accepted(OrderSide.BUY, 4, "100.00", "2026-01-01T00:00:00Z");
        Order bid100b = accepted(OrderSide.BUY, 6, "100.00", "2026-01-01T00:01:00Z");
        Order bid99 = accepted(OrderSide.BUY, 2, "99.00", "2026-01-01T00:02:00Z");
        Order ask101 = accepted(OrderSide.SELL, 3, "101.00", "2026-01-01T00:03:00Z");
        Order ask102 = accepted(OrderSide.SELL, 7, "102.00", "2026-01-01T00:04:00Z");

        OrderBookSnapshot snapshot = OrderBookSnapshot.from(List.of(bid99, ask102, bid100a, ask101, bid100b));

        assertThat(snapshot.bids()).containsExactly(new OrderBookLevel(new BigDecimal("100.00"), 10, 2), new OrderBookLevel(new BigDecimal("99.00"), 2, 1));
        assertThat(snapshot.asks()).containsExactly(new OrderBookLevel(new BigDecimal("101.00"), 3, 1), new OrderBookLevel(new BigDecimal("102.00"), 7, 1));
    }

    private Order accepted(OrderSide side, long quantity, String price, String time) {
        Order order = new Order("client", side + time, instrument, side, OrderType.LIMIT, quantity, new BigDecimal(price));
        order.accept(Instant.parse(time));
        return order;
    }
}
