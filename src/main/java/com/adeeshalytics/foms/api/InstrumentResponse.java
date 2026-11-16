package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.Instrument;
import java.math.BigDecimal;
import java.util.UUID;

public record InstrumentResponse(UUID id, String symbol, String name, String assetType, String currency, boolean active, BigDecimal tickSize) {
    static InstrumentResponse from(Instrument instrument) {
        return new InstrumentResponse(instrument.getId(), instrument.getSymbol(), instrument.getName(), instrument.getAssetType().name(), instrument.getCurrency(), instrument.isActive(), instrument.getTickSize());
    }
}
