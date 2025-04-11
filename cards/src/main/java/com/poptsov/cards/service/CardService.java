package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;

import java.util.List;

public interface CardService {
    CardResponseDto createCard(CardCreateDto dto, Long userId);
    List<CardResponseDto> getUserCards(Long userId);
    CardResponseDto getCardForUser(Long cardId, Long userId);
    CardResponseDto blockCard(Long cardId, Long userId);
    CardResponseDto activateCard(Long cardId, Long userId);
    List<CardResponseDto> getAllCards();
    void deleteCard(Long cardId);
}