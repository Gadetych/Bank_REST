package com.example.bankcards.service;

import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchAdminCard;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.not_found.CardNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    @Override
    public BankCardResponse createCard(Long ownerId) {
        log.debug("==> Creating card for ownerId: {}", ownerId);
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new UsernameNotFoundException("Не найден пользователь с id = " + ownerId));

        String cardNumber = CardNumberGenerator.generateCardNumber();
        BankCard card = BankCard.builder()
                .cardNumber(cardNumber)
                .owner(owner)
                .expirationDate(LocalDate.now().plusYears(3))
                .cardStatus(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        card = cardRepository.save(card);

        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Created card response: {}", response);
        return response;
    }

    @Override
    public BankCardResponse changeCardStatus(Long cardId, CardStatus cardStatus) {
        log.debug("==> Changing status for cardId: {} to status: {}", cardId, cardStatus);
        BankCard card = cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Не найдена карта с id = " + cardId));
        card.setCardStatus(cardStatus);

        card = cardRepository.save(card);

        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Updated card response: {}", response);
        return response;
    }

    @Override
    public void deleteCard(Long cardId) {
        log.debug("==> Deleting card with id: {}", cardId);
        cardRepository.deleteById(cardId);
        log.debug("<== Card deleted successfully");
    }

    @Override
    public Page<BankCardResponse> getAllCards(ParamSearchAdminCard paramSearch) {
        log.debug("==> Getting all cards with params: {}}", paramSearch);
        Page<BankCard> page = cardRepository.findAll(PageRequest.of(paramSearch.getOffset(), paramSearch.getLimit()));

        Page<BankCardResponse> response = page.map(Mapper::bankCardToBankCardResponse);
        log.debug("<== Mapped to response page");
        return response;
    }

    @Override
    public BankCardResponse findCardById(Long cardId) {
        log.debug("==> Getting card by id: {}}", cardId);
        BankCard bankCard = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Не найдена карта с id = " + cardId));
        BankCardResponse response = Mapper.bankCardToBankCardResponse(bankCard);
        log.debug("<== Mapped card response: {}", response);
        return response;
    }
}
