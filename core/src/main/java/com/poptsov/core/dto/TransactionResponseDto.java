package com.poptsov.core.dto;

import com.poptsov.core.model.TransactionStatus;
import com.poptsov.core.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Schema(description = "Ответ с данными транзакции")
public record TransactionResponseDto(
        @Schema(description = "ID транзакции", example = "1")
        Long id,
        @Schema(description = "Маскированный номер исходной карты", example = "**** **** **** 1234")
        String sourceCardMask,
        @Schema(description = "Маскированный номер целевой карты", example = "**** **** **** 5678")
        String targetCardMask,
        @Schema(description = "Сумма транзакции", example = "500.00")
        BigDecimal amount,
        @Schema(description = "Тип транзакции", example = "TRANSFER")
        String type,
        @Schema(description = "Статус транзакции", example = "COMPLETED")
        String status,
        @Schema(description = "Описание транзакции", example = "Перевод средств")
        String description,
        @Schema(description = "Дата и время создания транзакции", example = "2023-05-15T14:30:00")
        LocalDateTime createdAt
) {}