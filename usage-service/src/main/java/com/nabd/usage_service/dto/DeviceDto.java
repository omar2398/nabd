package com.nabd.usage_service.dto;

import lombok.Builder;

@Builder
public record DeviceDto(
        Long id,
        String name,
        String location,
        String type,
        Long userId,
        double energyConsumed
) {}
