package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.AssetType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record InstrumentRequest(
        @NotBlank @Pattern(regexp = "[A-Z0-9._-]{1,32}") String symbol,
        @NotBlank @Size(max = 255) String name,
        @NotNull AssetType assetType,
        @NotBlank @Pattern(regexp = "[A-Z]{3}") String currency,
        @PositiveOrZero @Digits(integer = 12, fraction = 8) BigDecimal tickSize,
        Boolean active) { }
