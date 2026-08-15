package com.nabd.user_service.repository;

import com.nabd.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = :email")
    public Optional<User> findByEmail(@Param("email") String  email);
}
