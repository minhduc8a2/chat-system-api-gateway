package com.ducle.api_gateway.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping(value = "/fallback", method = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
            RequestMethod.DELETE, RequestMethod.PATCH })
    public Mono<ResponseEntity<Map<String, Object>>> fallback() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Service is currently unavailable.");
        body.put("timestamp", Instant.now());
        body.put("status", 503);
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body));
    }

}