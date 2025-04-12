package com.poptsov.core.dto;

import com.poptsov.core.model.LimitType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Schema(description = "Запрос на установку лимита")
public record SetLimitRequestDto(
        @Schema(description = "ID карты", example = "1")  @NotNull Long cardId,
        @Schema(description = "Тип лимита", example = "DAILY") @NotNull LimitType limitType,
        @Schema(description = "Сумма лимита", example = "1000.00") @Positive BigDecimal amount
) {}