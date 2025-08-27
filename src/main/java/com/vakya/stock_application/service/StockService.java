package com.vakya.stock_application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.vakya.stock_application.model.StockTick;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class StockService {
    private final WebClient webClient;

    @Value("${alphavantage.api.key}")
    private String apiKey;

    public StockService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co").build();
    }

    public Mono<StockTick> getStockPrice(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .flatMap(node -> {
                    JsonNode quote = node.get("Global Quote");
                    if (quote == null || quote.isNull()) {
                        return Mono.empty();
                    }

                    String priceText = quote.path("05. price").asText();
                    String dateText = quote.path("07. latest trading day").asText();

                    if (priceText == null || priceText.isBlank()) {
                        return Mono.empty();
                    }

                    double price = Double.parseDouble(priceText);
                    StockTick tick = new StockTick();
                    tick.setSymbol(symbol);
                    tick.setPrice(price);

                    if (dateText != null && !dateText.isBlank()) {
                        tick.setTimestamp(java.time.LocalDateTime.now());
                        System.out.println("Response for " + symbol + ": " + node.toPrettyString());

//                        tick.setTimestamp(java.time.LocalDate.parse(dateText).atStartOfDay());
                    } else {
                        tick.setTimestamp(java.time.LocalDateTime.now()); // fallback
                    }

                    return Mono.just(tick);
                });
    }

}