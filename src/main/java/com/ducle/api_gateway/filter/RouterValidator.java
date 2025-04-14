package com.ducle.api_gateway.filter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.ducle.api_gateway.model.domain.MicroserviceManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.server.reactive.ServerHttpRequest;

@Component
@RequiredArgsConstructor
@Slf4j
public class RouterValidator {

        private final MicroserviceManager microserviceManager;

        public boolean isSecured(ServerHttpRequest request) {
                String path = request.getURI().getPath();
                log.info(microserviceManager.getUnprotectedEndpoints().toString());
                return microserviceManager.getUnprotectedEndpoints().stream()
                                .noneMatch(path::contains);
        }
}
