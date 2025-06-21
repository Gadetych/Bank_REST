package com.example.bankcards.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
