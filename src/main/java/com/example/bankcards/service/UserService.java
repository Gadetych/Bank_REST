package com.example.bankcards.service;

import com.example.bankcards.dto.user.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<UserResponse> findAllUsers(List<Long> ids, Integer from, Integer size);

    UserResponse getUserById(Long userId);

    void deleteUser(long userId);
}
