package com.poptsov.core.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardCreateDto {
    @NotBlank
    private String holderName;

    @NotNull
    @Future
    private LocalDate expirationDate;

    @PositiveOrZero
    private BigDecimal initialBalance;
}
