package com.nabd.usage_service.servicee;

import com.nabd.kafka.event.AlertingEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class MockKafkaMessageService {
  private final KafkaTemplate<String, AlertingEvent> kafkaTemplate;

  public MockKafkaMessageService(KafkaTemplate<String, AlertingEvent> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage() {
    AlertingEvent event =
        AlertingEvent.builder()
            .message("Energy consumption exceed  the  threshold")
            .threshold(4500)
            .userId(1L)
            .energyConsumed(5500)
            .email("test@nabd.com")
            .build();

    kafkaTemplate.send("energy-alerts", event);
  }
}
