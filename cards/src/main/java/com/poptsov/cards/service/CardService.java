package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.model.CardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CardService {

    CardResponseDto createCard(CardCreateDto request);
    Page<CardResponseDto> getAllCards(String status, Pageable pageable);
    CardResponseDto changeCardStatus(Long cardId, CardStatus newStatus);


    List<CardResponseDto> getUserCards();
    CardResponseDto userBlockCard(Long cardId);
    Page<TransactionResponseDto> getCardTransactions(Long cardId, Pageable pageable);
}