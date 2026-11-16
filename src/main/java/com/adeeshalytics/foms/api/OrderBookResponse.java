package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.OrderBookLevel;
import com.adeeshalytics.foms.service.OrderBookSnapshot;
import java.util.List;

public record OrderBookResponse(String symbol, List<OrderBookLevel> bids, List<OrderBookLevel> asks) {
    static OrderBookResponse from(String symbol, OrderBookSnapshot snapshot) { return new OrderBookResponse(symbol, snapshot.bids(), snapshot.asks()); }
}
