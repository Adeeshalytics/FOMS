package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.OrderSide;
import com.adeeshalytics.foms.domain.OrderType;
import java.math.BigDecimal;

public record SubmitOrderCommand(String clientId, String clientOrderId, String symbol, OrderSide side,
                                 OrderType orderType, long quantity, BigDecimal limitPrice) { }
