package com.nabd.insight_service.client;

import com.nabd.insight_service.dto.UsageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class UsageClient {
  private final RestTemplate restTemplate;
  private final String baseUrl;

  public UsageClient(RestTemplate restTemplate, @Value("${usage.service.url}") String baseUrl) {
    this.baseUrl = baseUrl;
    this.restTemplate = new RestTemplate();
  }

  public UsageDto getOverviewForXDays(Long userId, int days) {
    UriComponentsBuilder.fromPath(baseUrl);
    String url =
        UriComponentsBuilder.fromUriString(baseUrl)
            .path("/{userId}")
            .queryParam("days", days)
            .buildAndExpand(userId)
            .toUriString();
    ResponseEntity<UsageDto> response = restTemplate.getForEntity(url, UsageDto.class);
    return response.getBody();
  }
}
