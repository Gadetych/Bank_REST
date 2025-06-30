package com.example.bankcards.util;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardExpirationService {
    private final CardRepository cardRepository;

    // Запуск каждый день в 00:05
    @Scheduled(cron = "${schedule.interval-in-cron}")
    @Transactional
    public void checkExpiredCards() {
        List<BankCard> activeCards = cardRepository.findByCardStatusAndExpirationDateBefore(
                CardStatus.ACTIVE,
                LocalDate.now());
        activeCards.forEach(card -> card.setCardStatus(CardStatus.EXPIRED));

        cardRepository.saveAll(activeCards);
    }
}
