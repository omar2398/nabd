package com.nabd.user_service.controller;

import com.nabd.user_service.dto.UserRequestDto;
import com.nabd.user_service.dto.UserResponseDto;
import com.nabd.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
  @Autowired private final UserService userService;

  @PostMapping
  public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
  }
  @GetMapping("/{id}")
  public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") Long id){
    return ResponseEntity.status(HttpStatus.OK).body(userService.getUserById(id));
  }
  @PutMapping("/{id}")
  public ResponseEntity<UserResponseDto> updateUserById(Long id, UserRequestDto request){
    return ResponseEntity.ok(userService.updateUserById(id, request));
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserById(Long id){
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build();
  }
}
