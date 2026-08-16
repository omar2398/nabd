package com.nabd.device_service.service;

import com.nabd.device_service.dto.DeviceRequestDTO;
import com.nabd.device_service.dto.DeviceResponseDTO;
import com.nabd.device_service.exception.DeviceNotFoundException;
import com.nabd.device_service.mapper.DeviceMapper;
import com.nabd.device_service.repository.DeviceRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {
  private final DeviceRepository repo;
  private final DeviceMapper mapper;

  public DeviceResponseDTO createDevice(DeviceRequestDTO request) {
    return mapper.toDTO(repo.save(mapper.toEntity(request)));
  }

  public DeviceResponseDTO findDeviceById(Long id) {
    var device =
        repo.findById(id)
            .orElseThrow(() -> new DeviceNotFoundException("There is no device with id: " + id));
    return mapper.toDTO(device);
  }

  public DeviceResponseDTO updateDeviceById(Long id, DeviceRequestDTO request) {
    var device =
        repo.findById(id)
            .orElseThrow(() -> new DeviceNotFoundException("There is no device with id: " + id));
    return mapper.toDTO(mapper.updateEntity(device, request));
  }

  public void deleteDeviceById(Long id) {
    var device =
        repo.findById(id)
            .orElseThrow(() -> new DeviceNotFoundException("There is no device with id: " + id));
    repo.deleteById(id);
  }
}
