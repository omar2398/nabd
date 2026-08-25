package com.nabd.ingestion_service.service;

import com.nabd.ingestion_service.dto.EnergyUsageDTO;
import com.nabd.kafka.event.EnergyUsageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestionService {
  private final KafkaTemplate<String, EnergyUsageEvent> kTemplate;

  public void ingestEnergyUsage(EnergyUsageDTO request) {
    EnergyUsageEvent event =
        EnergyUsageEvent.builder()
            .deviceId(request.deviceId())
            .energyConsumed(request.energyConsumed())
            .timestamp(request.timestamp())
            .build();
    kTemplate.send(
        "energy-usage", event); // key isn't required here, because the ordering of the meessage isn't  required
    log.info("The energy usage ingested with even: {}", event);
  }
}
