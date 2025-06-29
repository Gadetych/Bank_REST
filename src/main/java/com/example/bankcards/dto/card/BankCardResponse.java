package com.example.bankcards.dto.card;

import com.example.bankcards.entity.enums.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class BankCardResponse {
    private Long id;
    private String cardNumber;
    private Long ownerId;
    private LocalDate expirationDate;
    private CardStatus cardStatus;
    private BigDecimal balance;
}
