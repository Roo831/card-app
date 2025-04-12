package com.poptsov.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
@Schema(description = "Запрос на перевод средств")
public record TransferRequestDto(
        @Schema(description = "ID исходной карты", example = "1")
        @NotNull Long sourceCardId,
        @Schema(description = "ID целевой карты", example = "2")
        @NotNull Long targetCardId,
        @Schema(description = "Сумма перевода", example = "500.00")
        @Positive BigDecimal amount,
        @Schema(description = "Описание перевода", example = "Перевод другу")
        String description
) {}
