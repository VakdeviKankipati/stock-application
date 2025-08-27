package com.vakya.stock_application.service;

import com.vakya.stock_application.model.StockTick;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class StockRedisService {
    private final ReactiveValueOperations<String, StockTick> valueOps;
    public StockRedisService(ReactiveValueOperations<String, StockTick> valueOps) {
        this.valueOps = valueOps;
    }

    public Mono<Boolean> saveStockTick(StockTick tick) {
        return valueOps.set(tick.getSymbol(), tick);
    }

    public Mono<StockTick> getStockTick(String symbol) {
        return valueOps.get(symbol);
    }
}

