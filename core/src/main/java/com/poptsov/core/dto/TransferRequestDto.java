package com.poptsov.core.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record TransferRequestDto(
        @NotNull Long sourceCardId,
        @NotNull Long targetCardId,
        @Positive BigDecimal amount,
        String description
) {}
