package com.example.bankcards.service;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchCard;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.forbidden.AccessDeniedCardException;
import com.example.bankcards.exception.forbidden.AccessDeniedUserException;
import com.example.bankcards.exception.not_found.CardNotFoundException;
import com.example.bankcards.exception.not_found.UserNotFoundException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new UserNotFoundException("Не найден пользователь с id = " + ownerId));

        BankCard card = getBankCard(owner);
        card = cardRepository.save(card);

        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Created card response: {}", response);
        return response;
    }

    private BankCard getBankCard(User owner) {
        String cardNumber = CardNumberGenerator.generateCardNumber();
        return BankCard.builder()
                .cardNumber(cardNumber)
                .owner(owner)
                .expirationDate(LocalDate.now().plusYears(3))
                .cardStatus(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();
    }


    @Override
    public BankCardResponse changeCardStatus(Long cardId, CardStatus cardStatus) {
        log.debug("==> Changing status for cardId: {} to status: {}", cardId, cardStatus);
        BankCard card = getBankCard(cardId);
        card.setCardStatus(cardStatus);

        card = cardRepository.save(card);

        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Updated card response: {}", response);
        return response;
    }

    private BankCard getBankCard(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Not found card by id = " + cardId));
    }

    @Override
    public void deleteCard(Long cardId) {
        log.debug("==> Deleting card with id: {}", cardId);
        cardRepository.deleteById(cardId);
        log.debug("<== Card deleted successfully");
    }

    @Override
    public BankCardResponse findCardById(Long cardId) {
        log.debug("==> Getting card by id: {}}", cardId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("Not found user by username = " + username));
        BankCard bankCard = getBankCard(cardId);
        checkRoleAdmin(user);
        checkExpiredCard(bankCard);
        BankCardResponse response = Mapper.bankCardToBankCardResponse(bankCard);
        log.debug("<== Mapped card response: {}", response);
        return response;
    }

    private void checkRoleAdmin(User user) {
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedUserException("Access denied. Insufficient rights");
        }
    }

    private void checkExpiredCard(BankCard card) {
        if (card.getExpirationDate().isBefore(LocalDate.now())) {
            card.setCardStatus(CardStatus.EXPIRED);
            cardRepository.save(card);
        }
    }

    @Override
    public Page<BankCardResponse> getAllCards(ParamSearchCard paramSearch) {
        log.debug("==> Getting all cards with params: {}}", paramSearch);
        Page<BankCard> page = cardRepository.findAll(PageRequest.of(paramSearch.getOffset(), paramSearch.getLimit()));
        Page<BankCardResponse> response = page.map(Mapper::bankCardToBankCardResponse);
        log.debug("<== Mapped to response page: {}", response);
        return response;
    }

    //    User

    @Override
    public BankCardResponse createCard(String username, BigDecimal balance) {
        log.debug("==> Creating card by username: {}, balance = {}}", username, balance);
        User owner = userRepository.findByUsername(username).orElseThrow(() ->
                new UserNotFoundException("User " + username + "not found"));
        BankCard card = getBankCard(owner);
        card.setBalance(balance);
        card = cardRepository.save(card);

        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Created card response: {}", response);
        return response;
    }

    @Override
    public BankCardResponse findUserCardById(Long cardId) {
        log.debug("==> Getting card by id: {}}", cardId);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BankCard bankCard = getBankCard(cardId);
        checkUsername(bankCard, username);
        checkExpiredCard(bankCard);
        BankCardResponse response = Mapper.bankCardToBankCardResponse(bankCard);
        log.debug("<== Mapped card response: {}", response);
        return response;
    }

    private void checkUsername(BankCard bankCard, String username) {
        if (!bankCard.getOwner().getUsername().equals(username)) {
            log.error("Card ownership validation failed: cardOwner={}, requester={}",
                    bankCard.getOwner().getUsername(), username);
            throw new AccessDeniedCardException("Access denied. Card belongs to another user");
        }
    }

    @Override
    public Page<BankCardResponse> getUserCards(ParamSearchCard paramSearch) {
        log.debug("==> Getting user cards with params: {}}", paramSearch);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Specification<BankCard> spec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("username"), username);
        PageRequest pageRequest = PageRequest.of(paramSearch.getOffset(), paramSearch.getLimit());
        Page<BankCard> page = cardRepository.findAll(spec, pageRequest);
        Page<BankCardResponse> response = page.map(Mapper::bankCardToBankCardResponse);
        log.debug("<== Mapped to response page: {}", response);
        return response;
    }

    @Override
    @Transactional
    public BankCardResponse requestBlockCard(Long cardId) {
        log.debug("==> Changing status for cardId: {} to status: {}", cardId, CardStatus.BLOCKED);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BankCard card = getBankCard(cardId);
        checkUsername(card, username);
        if (card.getCardStatus() != CardStatus.ACTIVE) {
            throw new IllegalStateException("Only active cards can be blocked");
        }

        card.setCardStatus(CardStatus.BLOCKED);
        card = cardRepository.save(card);
        BankCardResponse response = Mapper.bankCardToBankCardResponse(card);
        log.debug("<== Updated card response: {}", response);
        return response;
    }

    @Override
    public BalanceResponse getCardBalance(Long cardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        BankCard card = getBankCard(cardId);
        checkUsername(card, username);
        return new BalanceResponse(cardId, card.getBalance());
    }


}
