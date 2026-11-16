package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.Execution;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ExecutionResponse(UUID id, UUID buyOrderId, UUID sellOrderId, String symbol, long quantity, BigDecimal executionPrice, Instant executedAt) {
    static ExecutionResponse from(Execution execution) { return new ExecutionResponse(execution.getId(), execution.getBuyOrder().getId(), execution.getSellOrder().getId(), execution.getInstrument().getSymbol(), execution.getQuantity(), execution.getExecutionPrice(), execution.getExecutedAt()); }
}
