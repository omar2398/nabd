package com.nabd.user_service.dto;

import lombok.Data;

@Data
public class UserRequestDto {
    private String name;
    private String lastName;
    private String email;
    private String address;
    private boolean alerting;
    private double energyAlertingThreshold;
}
