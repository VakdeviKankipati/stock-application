package com.vakya.stock_application.config;

import com.vakya.stock_application.model.StockTick;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.DeserializationFeature;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, StockTick> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<StockTick> valueSerializer = new Jackson2JsonRedisSerializer<>(StockTick.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        valueSerializer.setObjectMapper(objectMapper);
        StringRedisSerializer keySerializer = new StringRedisSerializer();

        RedisSerializationContext<String, StockTick> context = RedisSerializationContext
                .<String, StockTick>newSerializationContext(keySerializer)
                .value(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveValueOperations<String, StockTick> reactiveValueOps(
            ReactiveRedisTemplate<String, StockTick> template) {
        return template.opsForValue();
    }
}
