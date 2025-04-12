package com.poptsov.core.dto;

import com.poptsov.core.model.TransactionStatus;
import com.poptsov.core.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponseDto(
        Long id,
        String sourceCardMask,
        String targetCardMask,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status,
        LocalDateTime createdAt) {}