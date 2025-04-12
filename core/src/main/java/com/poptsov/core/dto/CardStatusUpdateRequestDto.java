package com.poptsov.core.dto;

import com.poptsov.core.model.CardStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на изменение статуса карты")

public record CardStatusUpdateRequestDto(
        @Schema(description = "Новый статус карты", example = "BLOCKED") @NotNull CardStatus status) {}