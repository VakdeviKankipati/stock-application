package com.vakya.stock_application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/register", "/login", "/css/**").permitAll()
                        .pathMatchers("/stock/details").authenticated()
                        .anyExchange().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")                // Your login page URL
                        .authenticationSuccessHandler((webFilterExchange, authentication) -> {
                            // Redirect to /stocks/details after successful login
                            return webFilterExchange.getExchange().getResponse()
                                    .setComplete()
                                    .then(Mono.fromRunnable(() -> {
                                        webFilterExchange.getExchange().getResponse()
                                                .getHeaders()
                                                .setLocation(URI.create("/stocks/details"));
                                    }));
                        })
                        .authenticationFailureHandler((webFilterExchange, exception) -> {
                            // Redirect to login page on failure
                            return Mono.fromRunnable(() -> {
                                webFilterExchange.getExchange().getResponse()
                                        .getHeaders()
                                        .setLocation(URI.create("/login?error=true"));
                            });
                        })
                )
                .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

