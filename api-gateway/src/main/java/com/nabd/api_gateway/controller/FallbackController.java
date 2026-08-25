package com.nabd.api_gateway.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping("/default")
    public ResponseEntity<Map<String, Object>> fallback(HttpServletRequest request) {
        String path = request.getRequestURI();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "SERVICE_UNAVAILABLE",
                        "message", "This service is temporarily unavailable",
                        "path", path,
                        "timestamp", Instant.now().toString()
                ));
    }
}
