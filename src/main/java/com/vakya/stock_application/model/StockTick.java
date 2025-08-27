package com.vakya.stock_application.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;

@Getter
@Setter
@RedisHash("Stock")
@JsonIgnoreProperties(ignoreUnknown = true)
public class StockTick {
    private String symbol;
    private double price;
//    private LocalDateTime timestamp;
}
