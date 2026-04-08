package com.bootcamp.gateway.controller;

import com.bootcamp.gateway.response.SuccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;

@RestController
public class FallbackController {

    @GetMapping(value = "/fallback", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<SuccessResponse<String>> fallback() {

        SuccessResponse<String> response = SuccessResponse.<String>builder()
                .timestamp(Instant.now().toString())
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .message("Servicio temporalmente no disponible.")
                .path("/fallback")
                .data("Circuit Breaker activado en Gateway")
                .build();

        return Mono.just(response);
    }
}