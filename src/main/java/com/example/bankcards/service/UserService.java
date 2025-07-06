package com.example.bankcards.service;

import com.example.bankcards.dto.user.ParamSearchAdminUser;
import com.example.bankcards.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDetails loadUserByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Page<UserResponse> findAllUsers(ParamSearchAdminUser paramSearch);

    UserResponse getUserById(Long userId);

    void deleteUser(long userId);
}
