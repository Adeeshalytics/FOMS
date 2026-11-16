package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.OrderStatus;
import com.adeeshalytics.foms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class StatisticsService {
    private final InstrumentRepository instruments; private final OrderRepository orders; private final ExecutionRepository executions;
    public StatisticsService(InstrumentRepository instruments, OrderRepository orders, ExecutionRepository executions) { this.instruments = instruments; this.orders = orders; this.executions = executions; }
    @Transactional(readOnly = true) public Overview overview() { return new Overview(instruments.count(), orders.count(), executions.count()); }
    @Transactional(readOnly = true) public InstrumentStatistics forSymbol(String symbol) {
        var instrument = instruments.findBySymbol(symbol).orElseThrow(() -> new ResourceNotFoundException("instrument not found"));
        long active = orders.countByInstrumentIdAndStatusIn(instrument.getId(), List.of(OrderStatus.ACCEPTED, OrderStatus.PARTIALLY_FILLED));
        return new InstrumentStatistics(instrument.getSymbol(), orders.countByInstrumentId(instrument.getId()), active, executions.countByInstrumentId(instrument.getId()));
    }
    public record Overview(long instrumentCount, long orderCount, long executionCount) { }
    public record InstrumentStatistics(String symbol, long orderCount, long activeOrderCount, long executionCount) { }
}
