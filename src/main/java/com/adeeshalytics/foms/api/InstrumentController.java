package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.InstrumentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/instruments")
public class InstrumentController {
    private final InstrumentService instruments;
    public InstrumentController(InstrumentService instruments) { this.instruments = instruments; }
    @PostMapping
    public ResponseEntity<InstrumentResponse> create(@Valid @RequestBody InstrumentRequest request) {
        var result = instruments.create(request.symbol(), request.name(), request.assetType(), request.currency(), request.tickSize());
        return ResponseEntity.created(URI.create("/api/v1/instruments/" + result.getSymbol())).body(InstrumentResponse.from(result));
    }
    @GetMapping public List<InstrumentResponse> list() { return instruments.findAll().stream().map(InstrumentResponse::from).toList(); }
    @GetMapping("/{symbol}") public InstrumentResponse get(@PathVariable String symbol) { return InstrumentResponse.from(instruments.findBySymbol(symbol)); }
    @PutMapping("/{symbol}") public InstrumentResponse update(@PathVariable String symbol, @Valid @RequestBody InstrumentRequest request) {
        return InstrumentResponse.from(instruments.update(symbol, request.name(), request.assetType(), request.currency(), request.active() == null || request.active(), request.tickSize()));
    }
}
