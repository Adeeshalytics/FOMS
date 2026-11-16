package com.adeeshalytics.foms.service;

import java.math.BigDecimal;

/** Aggregated visible liquidity at a single price. */
public record OrderBookLevel(BigDecimal price, long quantity, int orderCount) { }
