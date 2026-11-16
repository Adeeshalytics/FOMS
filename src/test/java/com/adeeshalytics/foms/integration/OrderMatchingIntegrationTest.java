package com.adeeshalytics.foms.integration;

import com.adeeshalytics.foms.domain.*;
import com.adeeshalytics.foms.repository.ExecutionRepository;
import com.adeeshalytics.foms.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class OrderMatchingIntegrationTest {
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired private InstrumentService instruments;
    @Autowired private OrderService orders;
    @Autowired private ExecutionRepository executions;

    @Test
    void persistsAPartialExecutionAndBothOrderStatesAtomically() {
        instruments.create("MATCH", "Matching Test", AssetType.EQUITY, "USD", new BigDecimal("0.01"));
        var resting = orders.submit(new SubmitOrderCommand("seller", "ask-1", "MATCH", OrderSide.SELL, OrderType.LIMIT, 10, new BigDecimal("100.00"))).order();
        var incoming = orders.submit(new SubmitOrderCommand("buyer", "bid-1", "MATCH", OrderSide.BUY, OrderType.LIMIT, 4, new BigDecimal("101.00"))).order();

        assertThat(incoming.getStatus()).isEqualTo(OrderStatus.FILLED);
        assertThat(resting.getStatus()).isEqualTo(OrderStatus.PARTIALLY_FILLED);
        assertThat(resting.remainingQuantity()).isEqualTo(6);
        assertThat(executions.findAll()).singleElement().satisfies(execution -> {
            assertThat(execution.getQuantity()).isEqualTo(4);
            assertThat(execution.getExecutionPrice()).isEqualByComparingTo("100.00");
        });
    }
}
