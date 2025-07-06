package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchCard;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.service.CardService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/card")
@Slf4j
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {
    private final CardService cardService;

    @PostMapping("/{ownerId}")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardResponse createCard(@PathVariable Long ownerId) {
        log.info("==> ADMIN: Creating card for ownerId: {}", ownerId);
        BankCardResponse response = cardService.createCard(ownerId);
        log.info("<== ADMIN: Successfully created card: {}", response);
        return response;
    }

    @PutMapping("/{cardId}")
    public BankCardResponse changeCardStatus(@PathVariable Long cardId,
                                             @RequestParam CardStatus status) {
        log.info("==> ADMIN: Changing status for cardId: {} to: {}", cardId, status);
        BankCardResponse response = cardService.changeCardStatus(cardId, status);
        log.info("<== ADMIN: Successfully created card: {}", response);
        return response;
    }

    @DeleteMapping("/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCard(@PathVariable Long cardId) {
        log.info("==> ADMIN: Deleting card with id: {}", cardId);
        cardService.deleteCard(cardId);
        log.info("<== ADMIN: Successfully deleted card with id: {}", cardId);
    }

    @GetMapping
    public Page<BankCardResponse> getAllCards(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "10", required = false) @PositiveOrZero @Max(100) Integer limit) {
        ParamSearchCard paramSearch = new ParamSearchCard(offset, limit);
        log.info("==> ADMIN: Requesting cards list with {}", paramSearch);
        Page<BankCardResponse> response = cardService.getAllCards(paramSearch);
        log.info("<== ADMIN: Successfully created card: {}", response);
        return response;
    }

    @GetMapping("/{cardId}")
    public BankCardResponse getCardById(@PathVariable Long cardId) {
        log.info("==> ADMIN: Getting card by id = {}", cardId);
        BankCardResponse response = cardService.findCardById(cardId);
        log.info("<== ADMIN: Successfully created card: {}", response);
        return response;
    }
}