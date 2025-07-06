package com.example.bankcards.dto.transfer;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequest(
        @Positive
        Long fromCardId,
        @Positive
        Long toCardId,
        @Positive(message = "Amount must be positive")
        @DecimalMin(value = "0.01", message = "Minimum amount is 0.01")
        @Digits(integer = 10, fraction = 2, message = "Invalid amount format")
        BigDecimal amount) {
}
