package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.Instrument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderBookService {
    private final InstrumentService instruments;
    private final ActiveOrderBook orderBook;
    public OrderBookService(InstrumentService instruments, ActiveOrderBook orderBook) { this.instruments = instruments; this.orderBook = orderBook; }
    @Transactional(readOnly = true)
    public OrderBookSnapshot get(String symbol) {
        Instrument instrument = instruments.findBySymbol(symbol);
        return orderBook.find(instrument.getId()).orElseGet(() -> OrderBookSnapshot.from(java.util.List.of()));
    }
}
