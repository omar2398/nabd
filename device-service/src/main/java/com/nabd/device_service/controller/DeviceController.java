package com.nabd.device_service.controller;

import com.nabd.device_service.dto.DeviceRequestDTO;
import com.nabd.device_service.dto.DeviceResponseDTO;
import com.nabd.device_service.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/device")
public class DeviceController {
    public DeviceController(DeviceService service){
        this.service = service;
    }
    private final DeviceService service;
    @PostMapping
    public ResponseEntity<DeviceResponseDTO> createDevice(@RequestBody DeviceRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDevice(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> findDeviceById(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(service.findDeviceById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> updateDeviceById(@PathVariable(name = "id") Long id, DeviceRequestDTO request){
        return ResponseEntity.ok(service.updateDeviceById(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeviceById(@PathVariable(name = "id") Long id){
        service.deleteDeviceById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceResponseDTO>> getAllDevicesByUser(Long userId){
        return ResponseEntity.ok(service.getAllDevicesByUser(userId));
    }
}
