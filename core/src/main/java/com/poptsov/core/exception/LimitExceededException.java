package com.poptsov.core.exception;

import com.poptsov.core.model.LimitType;

import java.math.BigDecimal;

public class LimitExceededException extends RuntimeException {

    private final LimitType limitType;
    private final BigDecimal allowedAmount;

    public LimitExceededException(LimitType limitType, BigDecimal allowedAmount) {
        super(String.format("Limit exceeded for type: %s. Allowed amount: %s", limitType, allowedAmount));
        this.limitType = limitType;
        this.allowedAmount = allowedAmount;
    }

    public LimitType getLimitType() {
        return limitType;
    }

    public BigDecimal getAllowedAmount() {
        return allowedAmount;
    }
}


