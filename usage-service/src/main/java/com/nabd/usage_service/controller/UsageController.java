package com.nabd.usage_service.controller;

import com.nabd.usage_service.servicee.MockKafkaMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/usage")
public class UsageController {
    private final MockKafkaMessageService mockKafkaMessageService;

    public UsageController(MockKafkaMessageService mockKafkaMessageService) {
        this.mockKafkaMessageService = mockKafkaMessageService;
    }

    @GetMapping
    public ResponseEntity<String> sentKafkaMessageManually(){
        mockKafkaMessageService.sendMessage();
        return ResponseEntity.ok("Kafka message has been created");
    }
}
