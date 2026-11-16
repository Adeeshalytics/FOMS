package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.domain.OrderSide;
import com.adeeshalytics.foms.domain.OrderType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record OrderRequest(
        @NotBlank @Size(max = 100) String clientId,
        @NotBlank @Size(max = 100) String clientOrderId,
        @NotBlank @Pattern(regexp = "[A-Z0-9._-]{1,32}") String symbol,
        @NotNull OrderSide side,
        @NotNull OrderType orderType,
        @Positive @Max(1_000_000_000) long quantity,
        @Digits(integer = 12, fraction = 8) BigDecimal limitPrice) { }
