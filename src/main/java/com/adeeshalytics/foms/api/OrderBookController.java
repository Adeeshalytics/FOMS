package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.OrderBookService;
import com.adeeshalytics.foms.service.OrderBookLevel;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order-books")
public class OrderBookController {
    private final OrderBookService books;
    public OrderBookController(OrderBookService books) { this.books = books; }
    @GetMapping("/{symbol}") public OrderBookResponse get(@PathVariable String symbol) { return OrderBookResponse.from(symbol, books.get(symbol)); }
    @GetMapping("/{symbol}/bids") public List<OrderBookLevel> bids(@PathVariable String symbol) { return books.get(symbol).bids(); }
    @GetMapping("/{symbol}/asks") public List<OrderBookLevel> asks(@PathVariable String symbol) { return books.get(symbol).asks(); }
}
