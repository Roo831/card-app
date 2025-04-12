package com.poptsov.transactions.controller;

import com.poptsov.core.dto.TransactionResponseDto;
import com.poptsov.core.dto.TransferRequestDto;
import com.poptsov.transactions.service.TransactionService;
import com.poptsov.transactions.service.TransactionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Транзакции", description = "API для управления транзакциями")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Operation(summary = "Перевод между картами. Доступен когда на обеих картах установлены месячный и суточный лимит.", description = "Доступно для USER, ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Перевод успешно выполнен",
                    content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена"),
            @ApiResponse(responseCode = "422", description = "Недостаточно средств или превышен лимит")
    })
    @PostMapping("/transfer")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionResponseDto> transfer(
            @Valid @RequestBody TransferRequestDto request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(transactionService.transferBetweenCards(request));
    }

    @Operation(summary = "Получение истории транзакций", description = "Доступно для USER, ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Page<TransactionResponseDto>> getHistory(
            @RequestParam Long cardId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                transactionService.getCardTransactions(cardId, pageable)
        );
    }
}