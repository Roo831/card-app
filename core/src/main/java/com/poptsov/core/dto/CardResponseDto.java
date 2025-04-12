package com.poptsov.core.dto;

import com.poptsov.core.model.CardStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CardResponseDto(
        Long id,
        String maskedNumber,
        String holderName,
        LocalDate expirationDate,
        BigDecimal balance,
        CardStatus status) {}