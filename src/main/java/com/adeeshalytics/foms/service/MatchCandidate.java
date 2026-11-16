package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Order;
import java.math.BigDecimal;

public record MatchCandidate(Order restingOrder, long quantity, BigDecimal executionPrice) { }
