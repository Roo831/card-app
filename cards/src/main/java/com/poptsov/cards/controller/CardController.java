package com.poptsov.cards.controller;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.model.User;
import com.poptsov.cards.service.CardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")

public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponseDto createCard(
            @RequestBody @Valid CardCreateDto dto,
            @AuthenticationPrincipal User currentUser) {
        return cardService.createCard(dto, currentUser.getId());
    }

    @GetMapping
    public List<CardResponseDto> getUserCards(
            @AuthenticationPrincipal User currentUser) {
        return cardService.getUserCards(currentUser.getId());
    }

    @GetMapping("/{id}")
    public CardResponseDto getCard(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return cardService.getCardForUser(id, currentUser.getId());
    }

    @PatchMapping("/{id}/block")
    public CardResponseDto blockCard(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return cardService.blockCard(id, currentUser.getId());
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public CardResponseDto activateCard(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        return cardService.activateCard(id, currentUser.getId());
    }
}