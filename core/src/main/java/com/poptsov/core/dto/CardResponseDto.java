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

    public CardResponseDto() {
    }

    public CardResponseDto(Long id, String maskedNumber, String holderName, LocalDate expirationDate, BigDecimal balance, String status) {
        this.id = id;
        this.maskedNumber = maskedNumber;
        this.holderName = holderName;
        this.expirationDate = expirationDate;
        this.balance = balance;
        this.status = status;
    }
}