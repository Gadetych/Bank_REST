package com.example.bankcards.service;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.Transfer;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.exception.forbidden.AccessDeniedCardException;
import com.example.bankcards.exception.forbidden.CardNotActiveException;
import com.example.bankcards.exception.not_found.CardNotFoundException;
import com.example.bankcards.exception.validation.InsufficientFundsException;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.TransferRepository;
import com.example.bankcards.util.mapper.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {
    private final TransferRepository transferRepository;
    private final CardRepository cardRepository;

    @Override
    public TransferResponse createTransferBetweenYourCards(TransferRequest request) {
        log.info("==> Creating transfer between cards: fromCardId={}, toCardId={}, amount={}",
                request.fromCardId(), request.toCardId(), request.amount());

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.debug("Authenticated user: {}", username);

        log.debug("Validating source card: {}", request.fromCardId());
        BankCard fromCard = validateCard(request.fromCardId(), username);
        log.debug("Validating destination card: {}", request.toCardId());
        BankCard toCard = validateCard(request.toCardId(), username);

        if (fromCard.getId().equals(toCard.getId())) {
            log.error("Attempt to transfer to the same card: {}", fromCard.getId());
            throw new IllegalArgumentException("Cannot transfer to the same card");
        }

        log.debug("Checking balance: available={}, required={}",
                fromCard.getBalance(), request.amount());
        if (fromCard.getBalance().compareTo(request.amount()) < 0) {
            log.error("Insufficient funds for transfer: available={}, required={}",
                    fromCard.getBalance(), request.amount());
            throw new InsufficientFundsException("Insufficient funds");
        }

        log.debug("Updating balances: fromCard={}, toCard={}",
                fromCard.getCardNumber(), toCard.getCardNumber());
        fromCard.setBalance(fromCard.getBalance().subtract(request.amount()));
        toCard.setBalance(toCard.getBalance().add(request.amount()));

        Transfer transfer = Transfer.builder()
                .fromCard(fromCard)
                .toCard(toCard)
                .amount(request.amount())
                .timestamp(LocalDateTime.now())
                .build();

        log.debug("Saving transfer record");
        transfer = transferRepository.save(transfer);
        log.info("<== Transfer completed successfully: transferId={}", transfer.getId());

        return Mapper.transferToTransferResponse(transfer);
    }

    private BankCard validateCard(Long cardId, String username) {
        log.debug("==> Validating card: {}", cardId);
        BankCard card = getBankCard(cardId);
        checkUsername(card, username);
        checkCardStatusActive(card);
        log.debug("<== Card validated successfully: {}", cardId);
        return card;
    }

    private void checkUsername(BankCard bankCard, String username) {
        if (!bankCard.getOwner().getUsername().equals(username)) {
            log.error("Card ownership validation failed: cardOwner={}, requester={}",
                    bankCard.getOwner().getUsername(), username);
            throw new AccessDeniedCardException("Access denied. Card belongs to another user");
        }
    }

    private void checkCardStatusActive(BankCard card) {
        if (card.getCardStatus() != CardStatus.ACTIVE) {
            log.error("Card status validation failed: cardId={}, status={}",
                    card.getId(), card.getCardStatus());
            throw new CardNotActiveException("Access denied. Card is not active");
        }
    }

    private BankCard getBankCard(Long cardId) {
        try {
            return cardRepository.findById(cardId)
                    .orElseThrow(() -> {
                        log.error("Card not found: {}", cardId);
                        return new CardNotFoundException("Not found card by id = " + cardId);
                    });
        } catch (Exception e) {
            log.error("Error retrieving card: {}", e.getMessage());
            throw e;
        }
    }
}