package com.adeeshalytics.foms.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "instruments")
public class Instrument {
    @Id @GeneratedValue private UUID id;
    @Column(nullable = false, unique = true, length = 32) private String symbol;
    @Column(nullable = false) private String name;
    @Enumerated(EnumType.STRING) @Column(name = "asset_type", nullable = false) private AssetType assetType;
    @Column(nullable = false, length = 3) private String currency;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "tick_size", precision = 20, scale = 8) private BigDecimal tickSize;
    @Column(name = "created_at", nullable = false) private Instant createdAt;
    @Column(name = "updated_at", nullable = false) private Instant updatedAt;
    @Version private long version;

    protected Instrument() { }

    public Instrument(String symbol, String name, AssetType assetType, String currency, BigDecimal tickSize) {
        this.symbol = symbol;
        this.name = name;
        this.assetType = assetType;
        this.currency = currency;
        this.tickSize = tickSize;
    }

    @PrePersist void created() { createdAt = updatedAt = Instant.now(); }
    @PreUpdate void updated() { updatedAt = Instant.now(); }
    public UUID getId() { return id; }
    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public AssetType getAssetType() { return assetType; }
    public String getCurrency() { return currency; }
    public boolean isActive() { return active; }
    public BigDecimal getTickSize() { return tickSize; }
    public void update(String name, AssetType assetType, String currency, boolean active, BigDecimal tickSize) {
        this.name = name; this.assetType = assetType; this.currency = currency; this.active = active; this.tickSize = tickSize;
    }
}
