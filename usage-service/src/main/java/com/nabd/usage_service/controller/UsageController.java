package com.nabd.usage_service.controller;

import com.nabd.usage_service.dto.UsageDto;
import com.nabd.usage_service.servicee.MockKafkaMessageService;
import com.nabd.usage_service.servicee.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.Response;

@RestController
@RequestMapping("/api/v1/usage")
public class UsageController {
  private final MockKafkaMessageService mockKafkaMessageService;
  private final UsageService service;

  public UsageController(MockKafkaMessageService mockKafkaMessageService, UsageService service) {
    this.mockKafkaMessageService = mockKafkaMessageService;
    this.service = service;
  }

  @GetMapping
  public ResponseEntity<String> sentKafkaMessageManually() {
    mockKafkaMessageService.sendMessage();
    return ResponseEntity.ok("Kafka message has been created");
  }

  @GetMapping("/overview")
  public ResponseEntity<UsageDto> getOverview(
      @RequestParam(name = "days", defaultValue = "3") int days, Long userId) {
    return ResponseEntity.ok(service.getOverview(userId, days));
  }
}
