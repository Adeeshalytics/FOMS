package com.adeeshalytics.foms.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record AmendOrderRequest(@Positive @Max(1_000_000_000) long quantity,
                                @NotNull @Positive @Digits(integer = 12, fraction = 8) BigDecimal limitPrice) { }
