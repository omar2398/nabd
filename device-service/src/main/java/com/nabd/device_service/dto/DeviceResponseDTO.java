package com.nabd.device_service.dto;

import com.nabd.device_service.enumration.DeviceType;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponseDTO {
    private Long id;
    private String name;
    private String location;
    private DeviceType type;
    private Long userId;
}
