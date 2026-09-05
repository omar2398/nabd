package com.nabd.user_service.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}
