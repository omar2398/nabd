package com.nabd.usage_service.dto;

import lombok.Builder;

@Builder
public record UserDto(
    Long id,
    String name,
    String lastName,
    String email,
    String address,
    boolean alerting,
    double energyAlertingThreshold) {}
