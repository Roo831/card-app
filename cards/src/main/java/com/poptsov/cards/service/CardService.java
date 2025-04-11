package com.poptsov.cards.service;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;

import java.util.List;

public interface CardService {

    CardResponseDto createCard(CardCreateDto dto, Long userId);
    void blockCard(Long cardId, Long userId);
    List<CardResponseDto> getUserCards(Long userId);
}
