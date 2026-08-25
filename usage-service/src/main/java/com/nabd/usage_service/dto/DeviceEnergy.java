package com.nabd.usage_service.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceEnergy {
    Long deviceId;
    double energyConsumed;
    Long userId;
}
