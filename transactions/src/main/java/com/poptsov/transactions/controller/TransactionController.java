package com.poptsov.transactions.controller;

import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.dto.TransferRequestDto;
import com.poptsov.transactions.service.TransactionService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<TransactionResponseDto> transfer(
            @Valid @RequestBody TransferRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.transferBetweenCards(request));
    }

    @GetMapping("/history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransactionResponseDto>> getHistory(
            @RequestParam Long cardId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                transactionService.getCardTransactions(cardId, pageable)
        );
    }
}