package com.ducle.api_gateway.service;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("auth-service")
public class AuthServiceClient {
    
}
