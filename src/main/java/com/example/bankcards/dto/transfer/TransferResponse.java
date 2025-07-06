package com.example.bankcards.dto.transfer;

import java.math.BigDecimal;

public record TransferResponse(
        Long id,
        Long fromCardId,
        Long toCardId,
        BigDecimal amount) {
}
