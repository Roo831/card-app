package com.poptsov.core.dto;

import com.poptsov.core.model.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Ответ с данными карты")
public record CardResponseDto(
        @Schema(description = "ID карты", example = "1") Long id,
        @Schema(description = "Маскированный номер карты", example = "**** **** **** 1234") String maskedNumber,
        @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")  String holderName,
        @Schema(description = "Дата окончания действия", example = "2025-12-31")  LocalDate expirationDate,
        @Schema(description = "Текущий баланс", example = "1500.50")  BigDecimal balance,
        @Schema(description = "Статус карты", example = "ACTIVE")  CardStatus status) {}