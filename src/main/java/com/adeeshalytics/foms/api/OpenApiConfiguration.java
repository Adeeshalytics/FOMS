package com.adeeshalytics.foms.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "FOMS API", version = "v1", description = "Simulated financial order management system. No real trading."))
public class OpenApiConfiguration { }
