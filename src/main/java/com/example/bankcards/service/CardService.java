package com.example.bankcards.service;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchCard;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface CardService {
    BankCardResponse createCard(Long ownerId);

    BankCardResponse createCard(String username, BigDecimal balance);

    BankCardResponse changeCardStatus(Long cardId, CardStatus cardStatus);

    void deleteCard(Long cardId);

    Page<BankCardResponse> getAllCards(ParamSearchCard param);

    BankCardResponse findCardById(Long cardId);

    BankCardResponse findUserCardById(Long cardId);

    Page<BankCardResponse> getUserCards(ParamSearchCard paramSearch);

    BankCardResponse requestBlockCard(Long cardId);

    BalanceResponse getCardBalance(Long cardId);
}
