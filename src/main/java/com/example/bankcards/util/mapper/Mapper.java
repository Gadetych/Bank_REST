package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.dto.user.RegistrationRequestDto;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
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

    public BankCardResponse bankCardToBankCardResponse(BankCard entity) {
        return new BankCardResponse(entity.getId(),
                maskCardNumber(entity.getCardNumber()),
                entity.getOwner().getId(),
                entity.getExpirationDate(),
                entity.getCardStatus(),
                entity.getBalance());
    }

    private String maskCardNumber(String cardNumber) {
        String suffix = cardNumber.substring(cardNumber.length() - 4);
        return "**** **** **** " + suffix;
    }

    public TransferResponse transferToTransferResponse(Transfer entity) {
        return new TransferResponse(entity.getId(),
                entity.getFromCard().getId(),
                entity.getToCard().getId(),
                entity.getAmount());
    }
}
