package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.Order;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record OrderResponse(UUID id, String clientId, String clientOrderId, String symbol, String side, String orderType,
                            long originalQuantity, long filledQuantity, long remainingQuantity, BigDecimal limitPrice,
                            BigDecimal averageExecutionPrice, String status, Instant createdAt, Instant updatedAt) {
    static OrderResponse from(Order order) {
        return new OrderResponse(order.getId(), order.getClientId(), order.getClientOrderId(), order.getInstrument().getSymbol(), order.getSide().name(), order.getOrderType().name(), order.getOriginalQuantity(), order.getFilledQuantity(), order.remainingQuantity(), order.getLimitPrice(), order.getAverageExecutionPrice(), order.getStatus().name(), order.getCreatedAt(), order.getUpdatedAt());
    }
}
