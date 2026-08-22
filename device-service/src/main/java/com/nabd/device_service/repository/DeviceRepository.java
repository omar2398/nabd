package com.nabd.device_service.repository;

import com.nabd.device_service.dto.DeviceResponseDTO;
import com.nabd.device_service.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
    @Query(value = "SELECT d from Device d where d.userId = :userId")
    List<Device> findAllDevicesById(@Param(value = "userId") Long userId);
}
