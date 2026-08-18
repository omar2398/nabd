package com.nabd.usage_service.client;

import com.nabd.usage_service.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {
  private final RestTemplate template;
  private final String baseUrl;

  public UserClient(RestTemplate template, @Value("${client-user.url}") String baseUrl) {
    this.template = new RestTemplate();
    this.baseUrl = baseUrl;
  }

  public UserDto getUserById(Long id) {
    final String url = String.format("%s/%d", baseUrl, id);
    return template.getForObject(url, UserDto.class);
  }
}
