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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
/*
@ConditionalOnProperty(
        name = "simulation.enable",
        havingValue = "true",
        matchIfMissing = false
)
*/

public class ParallelDataSimulator implements CommandLineRunner {

  private final ExecutorService executorService;
  private final RestTemplate template = new RestTemplate();
  Random random = new Random();

  @Value("${simulation.endpoint}")
  private String ingestionEndpoint;

  @Value("${simulation.parallel-threads}")
  private int parallelThreads;

  @Value("${simulation.requests-per-interval}")
  private int requestsPerInterval;

  public ParallelDataSimulator() {
    this.executorService = Executors.newCachedThreadPool();
  }

  @Override
  public void run(String... args) throws Exception {
    log.info("parallel data simulator started");
    ((ThreadPoolExecutor) executorService).setCorePoolSize(parallelThreads);
  }

  @Scheduled(fixedRateString = "${simulation.interval-ms}")
  public void sendMockData() {
    int batchSize = requestsPerInterval / parallelThreads;
    int reminder = requestsPerInterval % parallelThreads;

    for (int i = 0; i < parallelThreads; i++) {
      int requestsForThread = batchSize + (i < reminder ? 1 : 0);
      executorService.submit(
          () -> {
            for (int j = 0; j < requestsForThread; j++) {
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
                log.info("mock data has sent using thread: {}", Thread.currentThread());
              } catch (Exception ex) {
                log.error("failed to send mock data because of: {}", ex.getMessage());
              }
            }
          });
    }
  }
}
