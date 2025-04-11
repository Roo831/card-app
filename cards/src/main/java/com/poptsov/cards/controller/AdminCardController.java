package com.poptsov.cards.controller;

import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.cards.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCardController {

    private final CardService cardService;

    @Autowired
    public AdminCardController(CardService cardService) {
        this.cardService = cardService;
    }


    @GetMapping
    public List<CardResponseDto> getAllCards() {
        return cardService.getAllCards();
    }


    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
    }
}