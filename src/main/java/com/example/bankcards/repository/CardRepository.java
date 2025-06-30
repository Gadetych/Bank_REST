package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<BankCard, Long> {

    List<BankCard> findByCardStatusAndExpirationDateBefore(CardStatus active, LocalDate now);
}
