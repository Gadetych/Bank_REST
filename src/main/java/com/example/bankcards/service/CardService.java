package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchAdminCard;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.domain.Page;

public interface CardService {
    BankCardResponse createCard(Long ownerId);

    BankCardResponse changeCardStatus(Long cardId, CardStatus cardStatus);

    void deleteCard(Long cardId);

    Page<BankCardResponse> getAllCards(ParamSearchAdminCard param);

    BankCardResponse findCardById(Long cardId);
}
