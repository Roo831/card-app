package com.poptsov.core.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardCreateDto(
        @NotNull Long userId,
        @NotBlank String holderName,
        @Future LocalDate expirationDate,
        @PositiveOrZero BigDecimal initialBalance) {}