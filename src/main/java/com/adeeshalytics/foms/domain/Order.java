package com.adeeshalytics.foms.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    public static final long MAX_QUANTITY = 1_000_000_000L;

    @Id @GeneratedValue private UUID id;
    @Column(name = "client_id", nullable = false, length = 100) private String clientId;
    @Column(name = "client_order_id", nullable = false, length = 100) private String clientOrderId;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "instrument_id", nullable = false) private Instrument instrument;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderSide side;
    @Enumerated(EnumType.STRING) @Column(name = "order_type", nullable = false) private OrderType orderType;
    @Column(name = "original_quantity", nullable = false) private long originalQuantity;
    @Column(name = "filled_quantity", nullable = false) private long filledQuantity;
    @Column(name = "limit_price", precision = 20, scale = 8) private BigDecimal limitPrice;
    @Column(name = "average_execution_price", precision = 20, scale = 8) private BigDecimal averageExecutionPrice;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private OrderStatus status = OrderStatus.NEW;
    @Column(name = "accepted_at") private Instant acceptedAt;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;

    protected Order() { }

    public Order(String clientId, String clientOrderId, Instrument instrument, OrderSide side, OrderType orderType,
                 long originalQuantity, BigDecimal limitPrice) {
        if (originalQuantity <= 0 || originalQuantity > MAX_QUANTITY) throw new IllegalArgumentException("quantity must be between 1 and " + MAX_QUANTITY);
        if (orderType == OrderType.LIMIT && (limitPrice == null || limitPrice.signum() <= 0)) throw new IllegalArgumentException("limit orders require a positive limit price");
        if (orderType == OrderType.MARKET && limitPrice != null) throw new IllegalArgumentException("market orders must not include a limit price");
        this.clientId = clientId; this.clientOrderId = clientOrderId; this.instrument = instrument; this.side = side;
        this.orderType = orderType; this.originalQuantity = originalQuantity; this.limitPrice = limitPrice;
    }

    @PrePersist void created() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updated() { updatedAt = Instant.now(); }

    public void accept(Instant at) {
        requireStatus(OrderStatus.NEW, "accept"); status = OrderStatus.ACCEPTED; acceptedAt = at;
    }
    public void applyFill(long quantity, BigDecimal price) {
        if (status.isTerminal()) throw new IllegalStateException("terminal orders cannot be filled");
        if (quantity <= 0 || quantity > remainingQuantity()) throw new IllegalArgumentException("fill quantity exceeds leaves quantity");
        long priorFilled = filledQuantity;
        filledQuantity += quantity;
        BigDecimal priorNotional = averageExecutionPrice == null ? BigDecimal.ZERO : averageExecutionPrice.multiply(BigDecimal.valueOf(priorFilled));
        averageExecutionPrice = priorNotional.add(price.multiply(BigDecimal.valueOf(quantity)))
                .divide(BigDecimal.valueOf(filledQuantity), 8, RoundingMode.HALF_UP);
        status = remainingQuantity() == 0 ? OrderStatus.FILLED : OrderStatus.PARTIALLY_FILLED;
    }
    public void cancel() {
        if (status != OrderStatus.ACCEPTED && status != OrderStatus.PARTIALLY_FILLED) throw new IllegalStateException("only accepted orders can be cancelled");
        status = OrderStatus.CANCELLED;
    }
    public void amend(long quantity, BigDecimal price, Instant acceptedAt) {
        if (status != OrderStatus.ACCEPTED && status != OrderStatus.PARTIALLY_FILLED) throw new IllegalStateException("only accepted orders can be amended");
        if (orderType != OrderType.LIMIT) throw new IllegalStateException("only limit orders can be amended");
        if (quantity < filledQuantity || quantity <= 0 || quantity > MAX_QUANTITY || price == null || price.signum() <= 0) throw new IllegalArgumentException("invalid amendment");
        originalQuantity = quantity; limitPrice = price; this.acceptedAt = acceptedAt;
        status = remainingQuantity() == 0 ? OrderStatus.FILLED : (filledQuantity == 0 ? OrderStatus.ACCEPTED : OrderStatus.PARTIALLY_FILLED);
    }
    private void requireStatus(OrderStatus expected, String action) { if (status != expected) throw new IllegalStateException("cannot " + action + " order in " + status + " status"); }
    public UUID getId() { return id; }
    public String getClientId() { return clientId; }
    public String getClientOrderId() { return clientOrderId; }
    public Instrument getInstrument() { return instrument; }
    public OrderSide getSide() { return side; }
    public OrderType getOrderType() { return orderType; }
    public long getOriginalQuantity() { return originalQuantity; }
    public long getFilledQuantity() { return filledQuantity; }
    public long remainingQuantity() { return originalQuantity - filledQuantity; }
    public BigDecimal getLimitPrice() { return limitPrice; }
    public BigDecimal getAverageExecutionPrice() { return averageExecutionPrice; }
    public OrderStatus getStatus() { return status; }
    public Instant getAcceptedAt() { return acceptedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
