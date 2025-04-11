package com.poptsov.core.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CardResponseDto {
    private Long id;
    private String maskedNumber;
    private String holderName;
    private LocalDate expirationDate;
    private BigDecimal balance;
    private String status;
}