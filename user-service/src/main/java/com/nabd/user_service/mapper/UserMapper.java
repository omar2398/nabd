package com.nabd.user_service.mapper;

import com.nabd.user_service.dto.UserRequestDto;
import com.nabd.user_service.dto.UserResponseDto;
import com.nabd.user_service.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public User toEntity(UserRequestDto requestDto) {
    return User.builder()
        .address(requestDto.getAddress())
        .email(requestDto.getEmail())
        .alerting(requestDto.isAlerting())
        .energyAlertingThreshold(requestDto.getEnergyAlertingThreshold())
        .lastName(requestDto.getLastName())
        .name(requestDto.getName())
        .password(requestDto.getPassword())
        .build();
  }


  public void updateEntity(UserRequestDto requestDto, User user) {
    user.setAddress(requestDto.getAddress());
    user.setEmail(requestDto.getEmail());
    user.setAlerting(requestDto.isAlerting());
    user.setEnergyAlertingThreshold(
            requestDto.getEnergyAlertingThreshold()
    );
    user.setLastName(requestDto.getLastName());
    user.setName(requestDto.getName());
  }

  public UserResponseDto toDto(User user) {
    return UserResponseDto.builder()
        .id(user.getId())
        .address(user.getAddress())
        .email(user.getEmail())
        .alerting(user.isAlerting())
        .energyAlertingThreshold(user.getEnergyAlertingThreshold())
        .lastName(user.getLastName())
        .name(user.getName())
        .build();
  }
}
