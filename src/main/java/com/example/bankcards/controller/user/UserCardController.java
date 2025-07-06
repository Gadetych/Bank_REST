package com.example.bankcards.controller.user;

import com.example.bankcards.dto.card.BalanceResponse;
import com.example.bankcards.dto.card.BankCardResponse;
import com.example.bankcards.dto.card.ParamSearchCard;
import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/user/cards")
@Slf4j
@Validated
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserCardController {
    private final CardService cardService;
    private final TransferService transferService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public BankCardResponse createCard(@RequestParam(defaultValue = "0.0", required = false) BigDecimal balance) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Creating card with balance {}", username, balance);
        BankCardResponse response = cardService.createCard(username, balance);
        log.info("<== USER {}: Returned card details for card={}",
                username, response.getCardNumber());
        return response;
    }

    @GetMapping()
    public Page<BankCardResponse> getUserCards(
            @RequestParam(defaultValue = "0", required = false) @PositiveOrZero Integer offset,
            @RequestParam(defaultValue = "10", required = false) @PositiveOrZero @Max(100) Integer limit) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Requesting cards list with offset={}, limit={}", username, offset, limit);

        ParamSearchCard paramSearch = new ParamSearchCard(offset, limit);
        Page<BankCardResponse> response = cardService.getUserCards(paramSearch);

        log.info("<== USER {}: Returned {} cards (total {})",
                username, response.getNumberOfElements(), response.getTotalElements());
        return response;
    }

    @GetMapping("/{cardId}")
    public BankCardResponse getCardById(@PathVariable @PositiveOrZero Long cardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Requesting card details for cardId={}", username, cardId);

        BankCardResponse response = cardService.findUserCardById(cardId);

        log.info("<== USER {}: Returned card details for card={}",
                username, response.getCardNumber());
        return response;
    }

    @PutMapping("/request-block/{cardId}")
    public BankCardResponse requestBlockCard(@PathVariable @PositiveOrZero Long cardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Requesting to block cardId={}", username, cardId);

        BankCardResponse response = cardService.requestBlockCard(cardId);

        log.info("<== USER {}: Card {} block request processed. New status: {}",
                username, response.getCardNumber(), response.getCardStatus());
        return response;
    }

    @GetMapping("/{cardId}/balance")
    public BalanceResponse getCardBalance(@PathVariable Long cardId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Requesting balance for cardId={}", username, cardId);

        BalanceResponse response = cardService.getCardBalance(cardId);

        log.info("<== USER {}: Returned balance for cardId={}", username, cardId);
        return response;
    }

    @PostMapping("/transfers")
    public TransferResponse createTransfer(@RequestBody
                                           @Valid TransferRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("==> USER {}: Initiating transfer from {} to {}, amount={}",
                username, request.fromCardId(), request.toCardId(), request.amount());

        TransferResponse response = transferService.createTransferBetweenYourCards(request);

        log.info("<== USER {}: Transfer completed. TransferId={}", username, response.id());
        return response;
    }
}
