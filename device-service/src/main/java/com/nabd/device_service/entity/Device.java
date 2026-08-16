package com.nabd.device_service.entity;

import com.nabd.device_service.enumration.DeviceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    @Enumerated(EnumType.STRING)
    private DeviceType type;
    @Column(name = "user_id", nullable = false)
    private Long userId;
}
