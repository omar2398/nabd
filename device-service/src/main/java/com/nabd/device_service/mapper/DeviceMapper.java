package com.nabd.device_service.mapper;

import com.nabd.device_service.dto.DeviceRequestDTO;
import com.nabd.device_service.dto.DeviceResponseDTO;
import com.nabd.device_service.entity.Device;
import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {
  public Device toEntity(DeviceRequestDTO request) {
    return Device.builder()
        .name(request.getName())
        .type(request.getType())
        .location(request.getLocation())
        .userId(request.getUserId())
        .build();
  }

  public DeviceResponseDTO toDTO(Device device) {
    return DeviceResponseDTO.builder()
        .id(device.getId())
        .name(device.getName())
        .type(device.getType())
        .location(device.getLocation())
        .userId(device.getUserId())
        .build();
  }

  public Device updateEntity(Device device, DeviceRequestDTO requestDTO) {
    device.setName(requestDTO.getName());
    device.setType(requestDTO.getType());
    device.setLocation(requestDTO.getLocation());
    device.setUserId(requestDTO.getUserId());
    return device;
  }
}
