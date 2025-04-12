package com.poptsov.core.dto;

import com.poptsov.core.model.LimitType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SetLimitRequestDto(
        @NotNull Long cardId,
        @NotNull LimitType limitType,
        @Positive BigDecimal amount
) {}