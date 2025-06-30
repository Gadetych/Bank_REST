package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.user.ParamSearchAdminUser;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.util.mapper.Mapper;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    // Admin
    @Override
    public Page<UserResponse> findAllUsers(ParamSearchAdminUser paramSearch) {
        log.debug("==> Getting all users with params: {}}", paramSearch);
        Page<User> page = userRepository.findAll(PageRequest.of(paramSearch.getOffset(), paramSearch.getLimit()));
        Page<UserResponse> response = page.map(Mapper::userToUserResponse);
        log.debug("<== Mapped to response page");
        return response;
    }

    @Override
    public UserResponse getUserById(Long userId) {
        log.debug("==> Getting user by id = {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Не найден пользователь с id = " + userId));
        UserResponse userResponse = Mapper.userToUserResponse(user);
        log.debug("<== User = {}", userResponse);
        return userResponse;
    }

    @Transactional
    @Override
    public void deleteUser(long userId) {
        log.debug("==> Deleting user: {}", userId);
        userRepository.deleteById(userId);
        log.debug("<== Deleted user: {}", userId);
    }
}
