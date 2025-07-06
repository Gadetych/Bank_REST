package com.example.bankcards.service;

import com.example.bankcards.dto.transfer.TransferRequest;
import com.example.bankcards.dto.transfer.TransferResponse;

public interface TransferService {
    TransferResponse createTransferBetweenYourCards(TransferRequest request);
}
