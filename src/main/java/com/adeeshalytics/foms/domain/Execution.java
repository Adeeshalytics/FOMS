package com.adeeshalytics.foms.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "executions")
public class Execution {
    @Id @GeneratedValue private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "buy_order_id", nullable = false) private Order buyOrder;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "sell_order_id", nullable = false) private Order sellOrder;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "instrument_id", nullable = false) private Instrument instrument;
    @Column(nullable = false) private long quantity;
    @Column(name = "execution_price", nullable = false, precision = 20, scale = 8) private BigDecimal executionPrice;
    @Column(name = "executed_at", nullable = false) private Instant executedAt;

    protected Execution() { }
    public Execution(Order buyOrder, Order sellOrder, long quantity, BigDecimal executionPrice, Instant executedAt) {
        this.buyOrder = buyOrder; this.sellOrder = sellOrder; this.instrument = buyOrder.getInstrument();
        this.quantity = quantity; this.executionPrice = executionPrice; this.executedAt = executedAt;
    }
    public UUID getId() { return id; }
    public Order getBuyOrder() { return buyOrder; }
    public Order getSellOrder() { return sellOrder; }
    public Instrument getInstrument() { return instrument; }
    public long getQuantity() { return quantity; }
    public BigDecimal getExecutionPrice() { return executionPrice; }
    public Instant getExecutedAt() { return executedAt; }
}
