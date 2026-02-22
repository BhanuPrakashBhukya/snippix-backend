package com.meloffy.gateway.config;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityWebFilterChain websocketSecurityChain(ServerHttpSecurity http) {
        ServerWebExchangeMatcher wsMatcher =
                new PathPatternParserServerWebExchangeMatcher("/ws/**");
        http
                .securityMatcher(wsMatcher)
                .cors(cors -> cors.disable())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex.anyExchange().permitAll());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain apiSecurityChain(ServerHttpSecurity http) {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(ex -> ex
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/auth/**").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(ServerHttpSecurity.OAuth2ResourceServerSpec::jwt);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public GlobalFilter userHeaderFilter() {
        return (exchange, chain) ->
                exchange.getPrincipal()
                        .cast(Authentication.class)
                        .flatMap(auth -> {

                            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                                String userId = jwtAuth.getToken().getSubject();

                                ServerWebExchange mutated =
                                        exchange.mutate()
                                                .request(r -> r.header("X-User-Id", userId))
                                                .build();

                                return chain.filter(mutated);
                            }

                            return chain.filter(exchange);
                        })
                        .switchIfEmpty(chain.filter(exchange));
    }
}
