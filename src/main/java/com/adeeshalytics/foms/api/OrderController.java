package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.OrderService;
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
    public OrderController(OrderService orders) { this.orders = orders; }
    @PostMapping public ResponseEntity<OrderResponse> submit(@Valid @RequestBody OrderRequest request) {
        var order = orders.submit(new SubmitOrderCommand(request.clientId(), request.clientOrderId(), request.symbol(), request.side(), request.orderType(), request.quantity(), request.limitPrice()));
        return ResponseEntity.created(URI.create("/api/v1/orders/" + order.getId())).body(OrderResponse.from(order));
    }
    @GetMapping public Page<OrderResponse> list(@PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) { return orders.list(pageable).map(OrderResponse::from); }
    @GetMapping("/{orderId}") public OrderResponse get(@PathVariable UUID orderId) { return OrderResponse.from(orders.find(orderId)); }
    @GetMapping("/client/{clientId}/{clientOrderId}") public OrderResponse getByClientOrder(@PathVariable String clientId, @PathVariable String clientOrderId) { return OrderResponse.from(orders.find(clientId, clientOrderId)); }
    @PatchMapping("/{orderId}") public OrderResponse amend(@PathVariable UUID orderId, @Valid @RequestBody AmendOrderRequest request) { return OrderResponse.from(orders.amend(orderId, request.quantity(), request.limitPrice())); }
    @DeleteMapping("/{orderId}") public ResponseEntity<Void> cancel(@PathVariable UUID orderId) { orders.cancel(orderId); return ResponseEntity.noContent().build(); }
}
