package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("==> Load user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new UsernameNotFoundException("Пользователь с именем " + username + " не найден");
                });
        log.debug("Successfully loaded user [{}] with roles: {}",
                username, user.getAuthorities());
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        log.debug("==> Checking if user exists by username: {}", username);
        boolean exists = userRepository.existsByUsername(username);
        log.debug("<== User existence by username [{}]: {}", username, exists);
        return exists;
    }

    @Override
    public boolean existsByEmail(String email) {
        log.debug("==> Checking if user exists by email: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        log.debug("<== User existence by email [{}]: {}", email, exists);
        return exists;
    }
}
