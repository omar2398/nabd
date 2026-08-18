package com.nabd.usage_service.client;

import com.nabd.usage_service.dto.DeviceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class DeviceClient {
  private final RestTemplate template;
  private final String baseUrl;

  public DeviceClient(@Value("${client-device.url}") String baseUrl) {
    this.template = new RestTemplate();
    this.baseUrl = baseUrl;
  }

  public DeviceDto getDeviceById(Long id) {
    String url = String.format("%s/%d", baseUrl, id);
    String newUrlApproach =
        UriComponentsBuilder.fromUriString(url).path("/{id}").buildAndExpand(id).toUriString();
    //The second one is more suites, actually i didn't use before but copilot suggested it to me.
    return template.getForObject(newUrlApproach, DeviceDto.class);
  }
}
