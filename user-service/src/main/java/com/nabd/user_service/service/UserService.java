package com.nabd.user_service.service;

import com.nabd.user_service.dto.UserRequestDto;
import com.nabd.user_service.dto.UserResponseDto;
import com.nabd.user_service.exception.UserAlreadyExistException;
import com.nabd.user_service.exception.UserNotFoundException;
import com.nabd.user_service.mapper.UserMapper;
import com.nabd.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository repo;
  private final UserMapper mapper;
  private final PasswordEncoder passwordEncoder;

  public UserResponseDto createUser(UserRequestDto request) {
    log.info("creating the user: {}", request);
    if (repo.findByEmail(request.getEmail()).isPresent()) {
      log.error("user email is already found {}", request.getEmail());
      throw new UserAlreadyExistException("User with this email is already exists");
    }
    request.setPassword(passwordEncoder.encode(request.getPassword()));
    var createdUser = repo.save(mapper.toEntity(request));
    log.info("user created{}", createdUser);
    return mapper.toDto(createdUser);
  }

  public UserResponseDto getUserById(Long id) {
    var user =
        repo.findById(id)
            .orElseThrow(() -> new UserNotFoundException("There is no user with this id: " + id));
    return mapper.toDto(user);
  }

  public UserResponseDto updateUserById(Long id, UserRequestDto request) {
    log.info("Updating user with id: {}", id);
    var user = repo.findById(id)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "There is no user with this id: " + id
                    )
            );
    repo.findByEmail(request.getEmail())
            .filter(existingUser -> !existingUser.getId().equals(id))
            .ifPresent(existingUser -> {
              log.error(
                      "Cannot update user {}. Email already belongs to user {}",
                      id,
                      existingUser.getId()
              );

              throw new UserAlreadyExistException(
                      "User with this email already exists"
              );
            });
    mapper.updateEntity(request, user);
    var updatedUser = repo.save(user);
    log.info("User updated successfully: {}", id);
    return mapper.toDto(updatedUser);
  }

  public void deleteUserById(Long id) {
    log.info("Deleting user with id: {}", id);
    var user = repo.findById(id)
            .orElseThrow(() ->
                    new UserNotFoundException(
                            "There is no user with this id: " + id
                    )
            );
    repo.delete(user);
    log.info("User deleted successfully: {}", id);
  }
}
