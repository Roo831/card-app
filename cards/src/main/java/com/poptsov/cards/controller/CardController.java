package com.poptsov.cards.controller;

import com.poptsov.core.dto.CardCreateDto;
import com.poptsov.cards.service.CardService;
import com.poptsov.core.dto.CardResponseDto;
import com.poptsov.core.dto.CardStatusUpdateRequestDto;
import com.poptsov.core.dto.TransactionResponseDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Карты", description = "API для управления банковскими картами")
public class CardController {
    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Создание новой карты", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Карта успешно создана",
                    content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CardCreateDto request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cardService.createCard(request));
    }

    @Operation(summary = "Получение всех карт", description = "Доступно только для ADMIN с возможностью фильтрации по статусу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<CardResponseDto>> getAllCards(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(status, pageable));
    }

    @Operation(summary = "Изменение статуса карты", description = "Доступно только для ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Статус успешно изменен",
                    content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PostMapping("/admin/{cardId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CardResponseDto> changeCardStatus(
            @PathVariable Long cardId,
            @Valid @RequestBody CardStatusUpdateRequestDto request) {
        return ResponseEntity.ok(cardService.changeCardStatus(cardId, request.status()));
    }

    @Operation(summary = "Получение карт пользователя", description = "Доступно только для USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено",
                    content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CardResponseDto>> getUserCards() {
        return ResponseEntity.ok(cardService.getUserCards());
    }

    @Operation(summary = "Блокировка карты пользователем", description = "Доступно только для USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Карта успешно заблокирована",
                    content = @Content(schema = @Schema(implementation = CardResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @PostMapping("/{cardId}/block")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CardResponseDto> userBlockCard(@PathVariable Long cardId) {
        return ResponseEntity.ok(cardService.userBlockCard(cardId));
    }

    @Operation(summary = "Получение транзакций по карте", description = "Доступно только для USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получено",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
            @ApiResponse(responseCode = "404", description = "Карта не найдена")
    })
    @GetMapping("/my/{cardId}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Page<TransactionResponseDto>> getCardTransactions(
            @PathVariable Long cardId,
            Pageable pageable) {
        return ResponseEntity.ok(cardService.getCardTransactions(cardId, pageable));
    }
}