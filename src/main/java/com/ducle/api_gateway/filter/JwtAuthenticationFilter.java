package com.ducle.api_gateway.filter;

import java.util.List;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.ducle.api_gateway.util.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter {

    private final JwtUtils jwtUtils;
    private final RouterValidator routerValidator;
    private final ObjectMapper objectMapper;

    private String extractToken(ServerHttpRequest request) {
        if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
            throw new RuntimeException("Missing Authorization Header");
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        log.info("Request Path: {}", request.getURI().getPath());
        if (!routerValidator.isSecured(request)) {
            log.info("No need to filter, request is not secured");
            return chain.filter(exchange);
        }
        try {
            String token = extractToken(request);
            if (token != null && jwtUtils.isTokenValid(token)) {
                String username = jwtUtils.extractUsername(token);
                Long userId = jwtUtils.extractUserId(token);
                List<String> roles = jwtUtils.extractRoles(token);

                ServerHttpRequest mutatedRequest = exchange.getRequest()
                        .mutate()
                        .header("X-User-Username", username)
                        .header("X-User-UserId", userId.toString())
                        .header("X-User-Roles", objectMapper.writeValueAsString(roles))
                        .build();

                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            } else {
                log.warn("Invalid token received");
                return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid Token");
            }
        } catch (Exception ex) {
            log.error("Error in JWT Filter: {}", ex.getMessage());
            return onError(exchange, HttpStatus.UNAUTHORIZED, "JWT Token error");
        }
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        log.warn("JWT Filter error: {}", message);
        return response.setComplete();
    }

}
