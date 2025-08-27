package com.vakya.stock_application.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Getter
@Setter
@RedisHash("Stock")
public class StockTick {
    private String symbol;
    private double price;
//    private LocalDateTime timestamp;
}
