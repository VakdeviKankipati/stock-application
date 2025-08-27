package com.vakya.stock_application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vakya.stock_application.model.StockTick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StockService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${alphavantage.api.key}")
    private String apiKey;
    public StockService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co").build();
        this.objectMapper = objectMapper;
    }

    public Mono<StockTick> getStockPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder .path("/query")
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol) .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    String price = json.split("\"05. price\": \"")[1].split("\"")[0];
                    StockTick tick = new StockTick();
                    tick.setSymbol(symbol);
//                    tick.setPrice(Double.parseDouble(price));
                    return tick;
                });
    }


}