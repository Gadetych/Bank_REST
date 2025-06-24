package com.example.bankcards.mapper;

import com.example.bankcards.dto.user.RegistrationRequestDto;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class Mapper {
    public User registrationRequestDtoToUser(RegistrationRequestDto dto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();
    }

    public UserResponse userToUserResponse(User entity) {
        return new UserResponse(entity.getId(), entity.getUsername(), entity.getEmail());
    }
}
