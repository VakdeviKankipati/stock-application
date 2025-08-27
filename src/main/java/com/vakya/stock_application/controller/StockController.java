package com.vakya.stock_application.controller;

import com.vakya.stock_application.service.StockRedisService;
import com.vakya.stock_application.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Controller
public class StockController {

    private final StockService stockService;
    private final StockRedisService redisService;

    public StockController(StockService stockService, StockRedisService redisService) {
        this.stockService = stockService;
        this.redisService = redisService;
    }

//    @GetMapping("/stock")
//    public Mono<String> getStock(@RequestParam(value = "symbols", required = false) String symbolsCsv,
//                                 Model model) {
//        List<String> symbols = symbolsCsv != null && !symbolsCsv.isBlank()
//                ? Arrays.stream(symbolsCsv.split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .toList()
//                : List.of("IBM", "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA");
//
//        return Flux.fromIterable(symbols)
//                .flatMap(symbol -> stockService.getStockPrice(symbol)
//                        .onErrorResume(ex -> redisService.getStockTick(symbol))
//                        .flatMap(tick -> tick != null
//                                ? redisService.saveStockTick(tick).thenReturn(tick)
//                                : Mono.empty()))
//                .collectList()
//                .doOnNext(ticks -> {
//                    model.addAttribute("stocks", ticks);
//                    if (ticks.isEmpty()) {
//                        model.addAttribute("message", "No data available. Check API key or rate limits.");
//                    }
//                })
//                .thenReturn("stocks");
//    }

    @GetMapping("/stock")
    public Mono<String> getStock(@RequestParam(value = "symbols", required = false) String symbolsCsv, Model model) {
        List<String> symbols = symbolsCsv != null && !symbolsCsv.isBlank() ?
                Arrays.stream(symbolsCsv.split(",")) .map(String::trim)
                        .filter(s -> !s.isEmpty()) .toList() : List.of("IBM", "AAPL", "MSFT", "GOOGL", "AMZN", "TSLA");
        return Flux.fromIterable(symbols)
                .flatMap(symbol -> stockService.getStockPrice(symbol)
                        .flatMap(tick -> redisService.saveStockTick(tick).thenReturn(tick))) .collectList()
                .doOnNext(ticks -> model.addAttribute("stocks", ticks))
                .thenReturn("stocks");
    }


}

