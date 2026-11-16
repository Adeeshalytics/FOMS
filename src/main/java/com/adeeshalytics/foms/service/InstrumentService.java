package com.adeeshalytics.foms.service;

import com.adeeshalytics.foms.domain.*;
import com.adeeshalytics.foms.repository.InstrumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class InstrumentService {
    private final InstrumentRepository instruments;
    public InstrumentService(InstrumentRepository instruments) { this.instruments = instruments; }
    @Transactional public Instrument create(String symbol, String name, AssetType assetType, String currency, BigDecimal tickSize) {
        if (instruments.existsBySymbol(symbol)) throw new IllegalStateException("instrument symbol already exists");
        return instruments.save(new Instrument(symbol, name, assetType, currency, tickSize));
    }
    @Transactional(readOnly = true) public List<Instrument> findAll() { return instruments.findAll(); }
    @Transactional(readOnly = true) public Instrument findBySymbol(String symbol) { return instruments.findBySymbol(symbol).orElseThrow(() -> new ResourceNotFoundException("instrument not found")); }
    @Transactional public Instrument update(String symbol, String name, AssetType assetType, String currency, boolean active, BigDecimal tickSize) {
        Instrument instrument = findBySymbol(symbol);
        instrument.update(name, assetType, currency, active, tickSize);
        return instrument;
    }
}
