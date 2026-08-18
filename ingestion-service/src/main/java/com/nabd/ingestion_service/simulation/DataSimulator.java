package com.nabd.ingestion_service.simulation;

import com.nabd.ingestion_service.dto.EnergyUsageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;

@Slf4j
@Component
@ConditionalOnProperty(
        name = "simulation.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class DataSimulator implements CommandLineRunner {
  private final RestTemplate template = new RestTemplate();
  private final Random random = new Random();

  @Value("${simulation.requests-per-interval}")
  private int requestsPerInterval;

  @Value("${simulation.endpoint}")
  private String ingestionEndpoint;

  @Override
  public void run(String... args) throws Exception {
    log.info("Data simulator started");
  }

  @Scheduled(fixedRateString = "${simulation.interval-ms}")
  public void sendMockData() {
    for (int i = 0; i < requestsPerInterval; i++) {
      EnergyUsageDTO dto =
          EnergyUsageDTO.builder()
              .deviceId(random.nextLong(1, 31))
              .energyConsumed(random.nextDouble(0, 10))
              .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
              .build();
      try {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EnergyUsageDTO> request = new HttpEntity<>(dto, headers);
        template.postForEntity(ingestionEndpoint, request, Void.class);
        log.info("mock data has sent");
      } catch (Exception ex) {
        log.error("failed to send mock data because of: {}", ex.getMessage());
      }
    }
  }
}
