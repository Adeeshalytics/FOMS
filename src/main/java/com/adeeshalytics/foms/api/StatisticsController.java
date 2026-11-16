package com.adeeshalytics.foms.api;

import com.adeeshalytics.foms.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {
    private final StatisticsService statistics;
    public StatisticsController(StatisticsService statistics) { this.statistics = statistics; }
    @GetMapping("/overview") public StatisticsService.Overview overview() { return statistics.overview(); }
    @GetMapping("/instruments/{symbol}") public StatisticsService.InstrumentStatistics instrument(@PathVariable String symbol) { return statistics.forSymbol(symbol); }
}
