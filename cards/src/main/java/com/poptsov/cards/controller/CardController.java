package com.poptsov.cards.controller;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.cards.service.CardService;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.dto.CardStatusUpdateRequestDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CardCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(request));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(status, pageable));
    }

    @PostMapping("/admin/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponseDto> changeCardStatus(
            @PathVariable Long cardId,
            @Valid @RequestBody CardStatusUpdateRequestDto request) {
        return ResponseEntity.ok(cardService.changeCardStatus(cardId, request.status()));
    }

    // USER endpoints
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardResponseDto>> getUserCards() {
        return ResponseEntity.ok(cardService.getUserCards());
    }

    @PostMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponseDto> userBlockCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.userBlockCard(cardId));
    }

//    @GetMapping("/my/{cardId}/transactions")
//    @PreAuthorize("hasRole('USER')")
//    public ResponseEntity<Page<TransactionResponse>> getCardTransactions(
//            @PathVariable Long cardId,
//            Pageable pageable) {
//        return ResponseEntity.ok(cardService.getCardTransactions(cardId, pageable));
//    }  // На данный момент нет реализации transaction модуля, метод закомментирован.
}