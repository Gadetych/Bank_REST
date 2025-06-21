package com.example.bankcards.mapper;

import com.example.bankcards.dto.RegistrationRequestDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;

@UtilityClass
public class Mapper {
    public static User registrationRequestDtoToUser(RegistrationRequestDto dto, PasswordEncoder passwordEncoder) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.USER)
                .build();
    }
}
