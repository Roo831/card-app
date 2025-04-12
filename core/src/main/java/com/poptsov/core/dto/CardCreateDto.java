package com.poptsov.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Данные для создания карты")
public record CardCreateDto(
        @Schema(description = "ID пользователя", example = "1") @NotNull Long userId,
        @Schema(description = "Имя владельца карты", example = "IVAN IVANOV")  @NotBlank String holderName,
        @Schema(description = "Дата окончания действия карты (должна быть в будущем)", example = "2025-12-31")  @Future LocalDate expirationDate,
        @Schema(description = "Начальный баланс карты", example = "1000.00") @PositiveOrZero BigDecimal initialBalance) {}