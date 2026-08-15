package com.nabd.user_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String address;
    private boolean alerting;
    private double energyAlertingThreshold;
}
