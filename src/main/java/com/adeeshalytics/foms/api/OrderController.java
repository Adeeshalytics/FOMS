package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.OrderService;
import com.adeeshalytics.foms.service.ExecutionService;
import com.adeeshalytics.foms.service.AuditEventService;
import com.adeeshalytics.foms.service.SubmitOrderCommand;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orders;
    private final ExecutionService executions;
    private final AuditEventService auditEvents;
    public OrderController(OrderService orders, ExecutionService executions, AuditEventService auditEvents) { this.orders = orders; this.executions = executions; this.auditEvents = auditEvents; }
    @PostMapping public ResponseEntity<OrderResponse> submit(@Valid @RequestBody OrderRequest request) {
        var submission = orders.submit(new SubmitOrderCommand(request.clientId(), request.clientOrderId(), request.symbol(), request.side(), request.orderType(), request.quantity(), request.limitPrice()));
        if (submission.replay()) return ResponseEntity.ok(OrderResponse.from(submission.order()));
        return ResponseEntity.created(URI.create("/api/v1/orders/" + submission.order().getId())).body(OrderResponse.from(submission.order()));
    }
    @GetMapping public Page<OrderResponse> list(@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return orders.list(pageable).map(OrderResponse::from); }
    @GetMapping("/{orderId}") public OrderResponse get(@PathVariable UUID orderId) { return OrderResponse.from(orders.find(orderId)); }
    @GetMapping("/{orderId}/executions") public Page<ExecutionResponse> executions(@PathVariable UUID orderId, @PageableDefault(size = 50, sort = "executedAt", direction = Sort.Direction.DESC) Pageable pageable) { return executions.forOrder(orderId, pageable).map(ExecutionResponse::from); }
    @GetMapping("/{orderId}/audit-events") public java.util.List<AuditEventResponse> auditEvents(@PathVariable UUID orderId) { return auditEvents.forOrder(orderId).stream().map(AuditEventResponse::from).toList(); }
    @GetMapping("/client/{clientId}/{clientOrderId}") public OrderResponse getByClientOrder(@PathVariable String clientId, @PathVariable String clientOrderId) { return OrderResponse.from(orders.find(clientId, clientOrderId)); }
    @PatchMapping("/{orderId}") public OrderResponse amend(@PathVariable UUID orderId, @Valid @RequestBody AmendOrderRequest request) { return OrderResponse.from(orders.amend(orderId, request.quantity(), request.limitPrice())); }
    @DeleteMapping("/{orderId}") public ResponseEntity<Void> cancel(@PathVariable UUID orderId) { orders.cancel(orderId); return ResponseEntity.noContent().build(); }
}
