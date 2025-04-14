package com.ducle.api_gateway.model.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "microservice")
@Data
public class MicroserviceManager {
    @Data
    public static class Microservice {
        private String name;
        private List<String> protectedEndpoints = new ArrayList<>();
        private List<String> unprotectedEndpoints = new ArrayList<>();
    }

    List<Microservice> services = new ArrayList<>();

    public List<String> getUnprotectedEndpoints() {
        return services.stream()
                .flatMap(service -> service.getUnprotectedEndpoints().stream()
                        .map(ep ->  ep))
                .toList();
    }

    public List<String> getProtectedEndpoints() {
        return services.stream()
                .flatMap(service -> service.getProtectedEndpoints().stream()
                        .map(ep -> ep))
                .toList();
    }

}
