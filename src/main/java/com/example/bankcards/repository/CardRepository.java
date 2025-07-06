package com.example.bankcards.repository;

import com.example.bankcards.entity.BankCard;
import com.example.bankcards.entity.enums.CardStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<BankCard, Long>, JpaSpecificationExecutor<BankCard> {

    List<BankCard> findByCardStatusAndExpirationDateBefore(CardStatus active, LocalDate now);

}
