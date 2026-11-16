package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class PriceTimeMatcherTest {
    private final Instrument instrument = new Instrument("ACME", "Acme", AssetType.EQUITY, "USD", new BigDecimal("0.01"));
    private final PriceTimeMatcher matcher = new PriceTimeMatcher();

    @Test
    void selectsBestPriceThenOldestOrderAndUsesRestingPrice() {
        Order oldAsk = accepted("seller-a", OrderSide.SELL, 5, "100.00", "2026-01-01T00:00:00Z");
        Order betterAsk = accepted("seller-b", OrderSide.SELL, 3, "99.00", "2026-01-01T00:00:02Z");
        Order incoming = accepted("buyer", OrderSide.BUY, 6, "101.00", "2026-01-01T00:00:03Z");
        List<MatchCandidate> matches = matcher.match(incoming, List.of(oldAsk, betterAsk));
        assertThat(matches).extracting(MatchCandidate::restingOrder).containsExactly(betterAsk, oldAsk);
        assertThat(matches).extracting(MatchCandidate::quantity).containsExactly(3L, 3L);
        assertThat(matches).extracting(MatchCandidate::executionPrice).extracting(BigDecimal::toPlainString).containsExactly("99.00", "100.00");
    }

    @Test
    void stopsAtBestSelfOrderRatherThanBypassingPriority() {
        Order selfAsk = accepted("buyer", OrderSide.SELL, 5, "100.00", "2026-01-01T00:00:00Z");
        Order thirdPartyAsk = accepted("seller", OrderSide.SELL, 5, "100.00", "2026-01-01T00:00:01Z");
        Order incoming = accepted("buyer", OrderSide.BUY, 5, "101.00", "2026-01-01T00:00:02Z");
        assertThat(matcher.match(incoming, List.of(selfAsk, thirdPartyAsk))).isEmpty();
    }

    @Test
    void doesNotMatchNonCrossingLimitPrices() {
        Order ask = accepted("seller", OrderSide.SELL, 5, "101.00", "2026-01-01T00:00:00Z");
        Order bid = accepted("buyer", OrderSide.BUY, 5, "100.00", "2026-01-01T00:00:01Z");
        assertThat(matcher.match(bid, List.of(ask))).isEmpty();
    }

    private Order accepted(String client, OrderSide side, long quantity, String price, String acceptedAt) {
        Order order = new Order(client, client + acceptedAt, instrument, side, OrderType.LIMIT, quantity, new BigDecimal(price));
        order.accept(Instant.parse(acceptedAt));
        return order;
    }
}
