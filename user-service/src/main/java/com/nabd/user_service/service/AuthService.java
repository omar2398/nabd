package com.nabd.user_service.service;

import com.nabd.user_service.dto.AuthRequestDto;
import com.nabd.user_service.dto.AuthResponseDto;
import com.nabd.user_service.exception.UserNotFoundException;
import com.nabd.user_service.repository.UserRepository;
import com.nabd.user_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDto login(AuthRequestDto request) {
        log.info("Login attempt for email: {}", request.getEmail());
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("No user found with this email"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());
        log.info("Login successful for email: {}", request.getEmail());
        return AuthResponseDto.builder().token(token).build();
    }
}
