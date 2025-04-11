package com.poptsov.cards.controller;

import com.poptsov.cards.service.CardService;
import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.model.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")

public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponseDto createCard(@RequestBody @Valid CardCreateDto dto,
                                      @AuthenticationPrincipal User user) {
        return cardService.createCard(dto, user.getId());
    }
}